package samsung.signature.signatureservice.friend.repository.query;

import java.util.List;
import java.util.Optional;

import samsung.signature.signatureservice.friend.dto.response.SignatureFriend;
import samsung.signature.signatureservice.member.domain.Member;
import samsung.signature.signatureservice.signature.dto.response.ToMemberInfo;

public interface FriendRepositoryCustom {
	List<SignatureFriend> findAllFriends(final Member member);

	Optional<ToMemberInfo> findFriendByToId(
		final Member loginMember,
		final Long friendId);

	List<ToMemberInfo> findAllFriendByToId(
		final Member loginMember,
		final List<Long> friendIds);
}
