package samsung.signature.signatureservice.signature.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import samsung.signature.common.exception.SignatureException;
import samsung.signature.common.utils.RedisUtils;
import samsung.signature.signatureservice.friend.domain.Friend;
import samsung.signature.signatureservice.friend.repository.FriendRepository;
import samsung.signature.signatureservice.global.client.WalletServiceClient;
import samsung.signature.signatureservice.member.repository.MemberRepository;
import samsung.signature.signatureservice.notification.domain.NotificationType;
import samsung.signature.signatureservice.notification.service.NotificationService;
import samsung.signature.signatureservice.signature.domain.*;
import samsung.signature.signatureservice.signature.dto.request.AddSignatureCardRequest;
import samsung.signature.signatureservice.signature.dto.request.AddSignatureCardToWalletRequest;
import samsung.signature.signatureservice.signature.dto.response.ExistsSignatureCardResponse;
import samsung.signature.signatureservice.signature.dto.response.SignatureHistoryDetailResponse;
import samsung.signature.signatureservice.signature.dto.response.SignatureHistoryListResponse;
import samsung.signature.signatureservice.signature.exception.SignatureDetailErrorCode;
import samsung.signature.signatureservice.signature.repository.SignatureDetailCardRepository;
import samsung.signature.signatureservice.signature.repository.SignatureDetailRepository;
import samsung.signature.signatureservice.signature.util.SignatureServiceUtil;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SignatureService {
	private final SignatureDetailRepository signatureDetailRepository;
	private final SignatureDetailCardRepository signatureDetailCardRepository;
	private final WalletServiceClient walletServiceClient;
	private final MemberRepository memberRepository;
	private final FriendRepository friendRepository;
	private final NotificationService notificationService;
	private final RedisTemplate<byte[], byte[]> redisTemplate;
	private final ObjectMapper objectMapper;
	private static final String SIGNATURE_STATUS_PREFIX = "signature-status:";

	public SignatureHistoryListResponse getSignatureHistoryList(String type, Long memberId) {
		if (type.equals("applied")) {
			return getAppliedHistoryList(memberId);
		} else if (type.equals("approved")) {
			return getApprovedHistoryList(memberId);
		} else {
			throw new SignatureException(SignatureDetailErrorCode.NOT_EXISTS_TYPE);
		}
	}

	public SignatureHistoryListResponse getAppliedHistoryList(Long memberId) {
		return signatureDetailRepository.findAllAppliedSignatureDetailByMemberId(
			memberId);

	}

	public SignatureHistoryListResponse getApprovedHistoryList(Long memberId) {
		return signatureDetailRepository.findAllApprovedSignatureDetailByMemberId(
			memberId);
	}

	@Transactional
	public SignatureHistoryDetailResponse getSignatureDetail(Long memberId, String type, Long signatureDetailId) {
		if (!(type.equals("approved") || type.equals("applied"))) {
			throw new SignatureException(SignatureDetailErrorCode.NOT_EXISTS_TYPE);
		}
		SignatureDetail signatureDetail = signatureDetailRepository.getReferenceById(signatureDetailId);
		Friend friend = getFriend(memberId,
			type.equals("approved")
				? signatureDetail.getFrom().getId()
				: signatureDetail.getTo().getId()
		);
		ProgressType progressType = isInProgressRequest(memberId,
			signatureDetailId, signatureDetail);
		return SignatureHistoryDetailResponse.of(
			signatureDetail,
			friend,
			progressType
		);
	}

	private ProgressType isInProgressRequest(
		final Long memberId,
		final Long signatureDetailId,
		final SignatureDetail signatureDetail
	) {
		ProgressType progressType;
		PermissionStatus permissionStatus = signatureDetail.getPermissionStatus();
		if (permissionStatus.equals(PermissionStatus.APPROVED)
			|| permissionStatus.equals(PermissionStatus.REJECTED)) {
			return null;
		}
		try {
			NotificationType noti = RedisUtils.get(
				redisTemplate,
				objectMapper,
				NotificationType.class,
				SIGNATURE_STATUS_PREFIX, signatureDetailId
			);
			progressType =
				noti == NotificationType.INPROGRESS_CONSIDER
					? ProgressType.INPROGRESS_CONSIDER
					: ProgressType.INPROGRESS_PICKCARD;

		} catch (SignatureException e) {
			progressType = ProgressType.INPROGRESS_SUBMIT;

			// 대리결제 요청 확인 알람
			if (!memberId.equals(signatureDetail.getFrom().getId())) {
				RedisUtils.put(
					redisTemplate,
					objectMapper,
					NotificationType.INPROGRESS_CONSIDER,
					SIGNATURE_STATUS_PREFIX, signatureDetailId
				);
				notificationService.publish(signatureDetail.getFrom().getId(),
					NotificationType.INPROGRESS_CONSIDER,
					signatureDetail);
			}
		}
		return progressType;
	}

	private Friend getFriend(Long memberId, Long friendId) {
		return friendRepository.findByFrom_IdAndTo_Id(memberId, friendId)
			.orElseThrow(() -> new SignatureException(SignatureDetailErrorCode.NOT_FOUND_SIGNATURE_DETAIL_HISTORY));
	}

	@Transactional
	public void exchangeValidateType(Long signatureDetailCardId, ValidateType type) {
		SignatureDetailCard signatureDetailCard = SignatureServiceUtil.getSignatureDetailCard(
			signatureDetailCardRepository, signatureDetailCardId);

		signatureDetailCard.exchangeIsValidate(type);

		// ONOFF + OFFON 알림
		SignatureDetail signatureDetail =
			SignatureServiceUtil.getSignatureDetailBySignatureCardId(
				signatureDetailRepository,
				signatureDetailCardId
			);
		notificationService.publish(signatureDetail.getFrom().getId(),
			type == ValidateType.ON ? NotificationType.ONOFF : NotificationType.OFFON,
			signatureDetail);
		signatureDetailCardRepository.save(signatureDetailCard);
	}

	@Transactional
	public void deleteApproval(Long signatureDetailCardId) {
		SignatureDetailCard signatureDetailCard = SignatureServiceUtil.getSignatureDetailCard(
			signatureDetailCardRepository, signatureDetailCardId);

		signatureDetailCard.softDelete();
		signatureDetailCardRepository.save(signatureDetailCard);
	}

	@Transactional
	public void addSignatureCard(
		final Long memberId,
		final AddSignatureCardRequest request) {

		String cardName = memberRepository.getReferenceById(memberId).getUserName() + " 님의 SIGNATURE 카드";

		// wallet 서버에 signature card 등록 요청
		AddSignatureCardToWalletRequest walletRequest = AddSignatureCardToWalletRequest
			.builder()
			.cardCompany(CardCompanyType.SIGNATURE)
			.cardName(cardName)
			.cardImg(request.cardImg())
			.build();

		try {
			walletServiceClient.addSignatureCard(memberId, walletRequest);
		} catch (FeignException e) {
			if (HttpStatus.BAD_REQUEST.value() == e.status()) {
				throw new SignatureException(SignatureDetailErrorCode.EXIST_SIGNATURE_CARD);
			}
		}

	}

	public ExistsSignatureCardResponse isExistsSignatureCard(Long memberId) {
		return ExistsSignatureCardResponse.builder()
			.isExists(walletServiceClient.isExistsSignatureCard(memberId).isExists())
			.build();
	}

}
