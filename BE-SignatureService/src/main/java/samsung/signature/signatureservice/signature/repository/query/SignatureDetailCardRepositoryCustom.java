package samsung.signature.signatureservice.signature.repository.query;

import java.util.List;
import java.util.Optional;

import samsung.signature.signatureservice.signature.domain.SignatureDetailCard;
import samsung.signature.signatureservice.signature.dto.response.GivenCardDetailListResposne;

public interface SignatureDetailCardRepositoryCustom {
	Optional<SignatureDetailCard> findApprovedCardByCardId(final Long memberId, final Long signatureDetailCardId);
	Optional<SignatureDetailCard> findAllApprovedCardsByCardId(final Long memberId, final Long signatureDetailCardId);

	List<Long> findAllGivenCardByMemberId(Long memberId);

	GivenCardDetailListResposne findAllGivenCardDetailByGivenCardIdAndMemberId(Long cardId, Long memberId);
}
