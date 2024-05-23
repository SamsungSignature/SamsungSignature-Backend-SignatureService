package samsung.signature.signatureservice.signature.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import samsung.signature.common.exception.SignatureException;
import samsung.signature.signatureservice.signature.domain.PermissionStatus;
import samsung.signature.signatureservice.signature.domain.SignatureDetail;
import samsung.signature.signatureservice.signature.domain.SignatureDetailCard;
import samsung.signature.signatureservice.signature.exception.SignatureDetailErrorCode;
import samsung.signature.signatureservice.signature.repository.SignatureDetailCardRepository;
import samsung.signature.signatureservice.signature.repository.SignatureDetailRepository;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SignatureServiceUtil {
	public static SignatureDetailCard getSignatureDetailCard(
		final SignatureDetailCardRepository signatureDetailCardRepository,
		final Long signatureDetailCardId
	){
		return signatureDetailCardRepository.findById(signatureDetailCardId)
			.orElseThrow(() -> new SignatureException(SignatureDetailErrorCode.NO_SIGNATURE_DETAIL_CARD));
	}

	public static SignatureDetail getSignatureDetail(
		final SignatureDetailRepository signatureDetailRepository,
		final Long signatureDetailId
	){
		return signatureDetailRepository.findById(signatureDetailId)
			.orElseThrow(() -> new SignatureException(SignatureDetailErrorCode.NOT_FOUND_SIGNATURE_DETAIL));
	}

	public static SignatureDetail changeStatusSignatureDetail(
		final SignatureDetailRepository signatureDetailRepository,
		final Long signatureDetailId,
		final PermissionStatus permissionStatus
	){
		SignatureDetail signatureDetail = getSignatureDetail(
			signatureDetailRepository,
			signatureDetailId
		);
		return signatureDetail.updateProcess(permissionStatus);
	}

	public static SignatureDetail getSignatureDetailBySignatureCardId(
		final SignatureDetailRepository signatureDetailRepository,
		final Long signatureDetailCardId
	) {
		return signatureDetailRepository.findBySignatureDetailCardId(signatureDetailCardId)
			.orElseThrow(() -> new SignatureException(SignatureDetailErrorCode.NO_SIGNATURE_DETAIL_CARD));
	}
}
