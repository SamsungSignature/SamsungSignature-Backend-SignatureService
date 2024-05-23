package samsung.signature.signatureservice.signature.repository.query;

import java.util.List;
import java.util.Optional;

import samsung.signature.signatureservice.member.domain.Member;
import samsung.signature.signatureservice.signature.dto.response.DelegatedCard;
import samsung.signature.signatureservice.signature.dto.response.SignatureHistoryListResponse;

public interface SignatureDetailRepositoryCustom {
	SignatureHistoryListResponse findAllAppliedSignatureDetailByMemberId(Long memberId);

	SignatureHistoryListResponse findAllApprovedSignatureDetailByMemberId(Long memberId);

	List<DelegatedCard> findAllApprovedAndUsableByMemberId(final long memberId);

	boolean invalidSignatureDetail(final Long signatureDetailId);

	Optional<Member> findMemberBySignatureDetailCardId(Long signatureDetailCardId);
}
