package samsung.signature.signatureservice.signature.util;

import lombok.NoArgsConstructor;
import samsung.signature.common.exception.SignatureException;
import samsung.signature.signatureservice.signature.domain.SignatureDetailCard;
import samsung.signature.signatureservice.signature.exception.SignatureDetailErrorCode;
import samsung.signature.signatureservice.signature.repository.SignatureDetailCardRepository;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class SignatureDetailCardServiceUtil {
	public static SignatureDetailCard findDelegatedCard(
		final SignatureDetailCardRepository signatureDetailCardRepository,
		final Long memberId,
		final Long signatureDetailCardId
	) {
		return signatureDetailCardRepository.findApprovedCardByCardId(memberId, signatureDetailCardId)
			.orElseThrow(() -> new SignatureException(SignatureDetailErrorCode.NOT_FOUND_CARD));
	}
	public static SignatureDetailCard findApprovedDelegatedCard(
		final SignatureDetailCardRepository signatureDetailCardRepository,
		final Long memberId,
		final Long signatureDetailCardId
	) {
		return signatureDetailCardRepository.findAllApprovedCardsByCardId(memberId, signatureDetailCardId)
			.orElseThrow(() -> new SignatureException(SignatureDetailErrorCode.NOT_FOUND_CARD));
	}
}
