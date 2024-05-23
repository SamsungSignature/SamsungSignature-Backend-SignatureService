package samsung.signature.signatureservice.signature.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import samsung.signature.common.exception.SignatureException;
import samsung.signature.signatureservice.global.client.WalletServiceClient;
import samsung.signature.signatureservice.member.domain.Member;
import samsung.signature.signatureservice.notification.domain.NotificationType;
import samsung.signature.signatureservice.notification.service.NotificationService;
import samsung.signature.signatureservice.signature.domain.PermissionStatus;
import samsung.signature.signatureservice.signature.domain.SignatureDetail;
import samsung.signature.signatureservice.signature.domain.SignatureDetailCard;
import samsung.signature.signatureservice.signature.exception.SignatureDetailErrorCode;
import samsung.signature.signatureservice.signature.repository.SignatureDetailCardRepository;
import samsung.signature.signatureservice.signature.repository.SignatureDetailRepository;
import samsung.signature.signatureservice.signature.util.SignatureServiceUtil;

@RequiredArgsConstructor
@Transactional
@Service
public class SignatureExpirationHandlerService {
	private final SignatureDetailRepository signatureDetailRepository;
	private final SignatureDetailCardRepository signatureDetailCardRepository;
	private final WalletServiceClient walletServiceClient;
	private final NotificationService notificationService;

	public void handleActiveTokenAfterLimitMinute(Long signatureDetailCardId) {
		Member member = signatureDetailRepository.findMemberBySignatureDetailCardId(signatureDetailCardId)
			.orElseThrow(() -> new SignatureException(SignatureDetailErrorCode.NOT_FOUND_SIGNATURE_DETAIL));
		walletServiceClient.deleteSignatureToken(member.getId());
	}

	public void handleSignatureTokenAfterLimitDate(Long signatureDetailCardId) {
		SignatureDetailCard signatureDetailCard = SignatureServiceUtil.getSignatureDetailCard(
			signatureDetailCardRepository, signatureDetailCardId);
		signatureDetailCard.unValidate();
		SignatureDetail signatureDetail =
			SignatureServiceUtil.getSignatureDetailBySignatureCardId(
				signatureDetailRepository,
				signatureDetailCardId
			);
		notificationService.publish(
			signatureDetail.getFrom().getId(),
			NotificationType.REQUEST_EXPIRED,
			signatureDetail
		);
	}

	public void handleRequestAfterOneDay(Long signatureDetailId) {
		SignatureDetail signatureDetail =
			SignatureServiceUtil.changeStatusSignatureDetail(
				signatureDetailRepository,
				signatureDetailId,
				PermissionStatus.REJECTED
			);
		notificationService.publish(
			signatureDetail.getFrom().getId(),
			NotificationType.REQUEST_EXPIRED,
			signatureDetail
		);
	}
}
