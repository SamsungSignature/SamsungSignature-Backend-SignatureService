package samsung.signature.signatureservice.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import samsung.signature.common.exception.SignatureException;
import samsung.signature.signatureservice.encryption.HybridTokenProvider;
import samsung.signature.signatureservice.encryption.SignatureTokenUtil;
import samsung.signature.signatureservice.global.client.WalletServiceClient;
import samsung.signature.signatureservice.member.repository.MemberRepository;
import samsung.signature.signatureservice.member.repository.PrivateKeyRepository;
import samsung.signature.signatureservice.notification.domain.NotificationType;
import samsung.signature.signatureservice.notification.service.NotificationService;
import samsung.signature.signatureservice.payment.domain.SignatureActiveToken;
import samsung.signature.signatureservice.payment.dto.request.SignaturePaymentInfo;
import samsung.signature.signatureservice.payment.repository.SignatureActiveTokenRepository;
import samsung.signature.signatureservice.signature.domain.SignatureDetail;
import samsung.signature.signatureservice.signature.domain.SignatureDetailCard;
import samsung.signature.signatureservice.signature.domain.SignatureToken;
import samsung.signature.signatureservice.signature.domain.SignatureTokenInfo;
import samsung.signature.signatureservice.signature.domain.SignatureType;
import samsung.signature.signatureservice.signature.exception.SignatureDetailErrorCode;
import samsung.signature.signatureservice.signature.repository.SignatureDetailCardRepository;
import samsung.signature.signatureservice.signature.repository.SignatureDetailRepository;
import samsung.signature.signatureservice.signature.repository.SignatureTokenInfoRepository;
import samsung.signature.signatureservice.signature.util.SignatureDetailCardServiceUtil;
import samsung.signature.signatureservice.signature.util.SignatureServiceUtil;

@RequiredArgsConstructor
@Service
public class SignaturePaymentService {
	private final SignatureDetailCardRepository signatureDetailCardRepository;
	private final SignatureDetailRepository signatureDetailRepository;
	private final SignatureActiveTokenRepository signatureActiveTokenRepository;
	private final WalletServiceClient walletServiceClient;
	private final SignatureTokenInfoRepository signatureTokenInfoRepository;
	private final HybridTokenProvider hybridTokenProvider;
	private final MemberRepository memberRepository;
	private final PrivateKeyRepository privateKeyRepository;
	private final NotificationService notificationService;

	public void signatureTokenPut(
		final Long memberId,
		final Long signatureDetailCardId
	) {
		// 선택한 카드가 유효한지 체크
		SignatureDetailCard signatureDetailCard =
			SignatureDetailCardServiceUtil.findDelegatedCard(
				signatureDetailCardRepository,
				memberId,
				signatureDetailCardId
			);

		// 카드 토큰이 있는지 체크
		SignatureTokenInfo signatureTokenInfo = signatureTokenInfoRepository.findById(signatureDetailCard.getId())
			.orElseThrow(() -> new IllegalArgumentException("카드 토큰이 존재하지 않습니다."));
		// 5분 동안 활성화된 시그니처 토큰 저장
		signatureActiveTokenRepository.save(SignatureActiveToken.from(signatureTokenInfo));
		// 시그니처 카드에 시그니처 토큰 저장
		walletServiceClient.saveSignatureToken(memberId, signatureTokenInfo.getEncodedToken());
	}

	@Transactional
	public String useSignatureCard(final SignaturePaymentInfo signaturePaymentRequest) {
		try {
			// 시그니처 토큰 활성화 확인
			SignatureActiveToken signatureActiveToken = validateSignatureToken(signaturePaymentRequest.cardToken());

			// 시그니처 토큰 디코딩
			SignatureToken signatureToken = decodeSignatureToken(signatureActiveToken.getSignatureTokenInfo());

			// 제약조건 확인
			validateAmount(signaturePaymentRequest.amount(), signatureToken.getLimitAmount());

			// 즉시 사용 타입일 경우 처리
			processInstantType(signatureActiveToken);

			// 시그니처 결제 알람
			SignatureDetail signatureDetail = SignatureServiceUtil.getSignatureDetail(
				signatureDetailRepository,
				signatureActiveToken.getSignatureTokenInfo().getSignatureDetailId()
			);
			notificationService.publishPayment(
				signatureDetail.getFrom().getId(),
				NotificationType.PAYMENT,
				signatureDetail,
				signaturePaymentRequest.amount()
			);
			// 카드 토큰 반환
			return signatureToken.getCardToken();
		} catch (SignatureException e) {
			throw e;
		} catch (Exception e) {
			throw new SignatureException(SignatureDetailErrorCode.NOT_VALIDATE_SIGNATURE_TOKEN);
		}
	}

	private SignatureActiveToken validateSignatureToken(String cardToken) {
		return signatureActiveTokenRepository.findByToken(cardToken)
			.orElseThrow(() -> new SignatureException(SignatureDetailErrorCode.NOT_VALIDATE_SIGNATURE_TOKEN));
	}

	private SignatureToken decodeSignatureToken(SignatureTokenInfo signatureTokenInfo) throws Exception {
		return SignatureTokenUtil.decodeSignatureToken(
			hybridTokenProvider,
			memberRepository,
			privateKeyRepository,
			signatureDetailRepository,
			signatureTokenInfo
		);
	}

	private void validateAmount(Integer amount, Integer limitAmount) {
		if (amount.compareTo(limitAmount) > 0) {
			throw new SignatureException(SignatureDetailErrorCode.OVER_LIMIT_PRICE);
		}
	}

	private void processInstantType(SignatureActiveToken signatureActiveToken) {
		if (SignatureType.INSTANT.equals(signatureActiveToken.getSignatureTokenInfo().getSignatureType())) {
			SignatureDetailCard signatureDetailCard = SignatureServiceUtil.getSignatureDetailCard(
				signatureDetailCardRepository,
				signatureActiveToken.getId()
			);
			signatureDetailCard.unValidate();
		}
	}
}
