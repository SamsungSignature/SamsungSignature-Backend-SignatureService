package samsung.signature.signatureservice.member.repository.query;

import java.util.List;
import java.util.Set;

import samsung.signature.signatureservice.member.domain.Member;

public interface MemberRepositoryCustom {
	List<Member> findAllFriendsByPhoneNumbers(Set<String> phoneNumbers);
}
