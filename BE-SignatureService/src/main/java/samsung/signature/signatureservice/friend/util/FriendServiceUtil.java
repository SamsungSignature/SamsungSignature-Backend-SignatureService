package samsung.signature.signatureservice.friend.util;

import lombok.NoArgsConstructor;
import samsung.signature.common.exception.SignatureException;
import samsung.signature.signatureservice.friend.repository.FriendRepository;
import samsung.signature.signatureservice.member.domain.Member;
import samsung.signature.signatureservice.member.exception.MemberErrorCode;
import samsung.signature.signatureservice.signature.dto.response.ToMemberInfo;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class FriendServiceUtil {
	public static ToMemberInfo getMemberFriend(
		final FriendRepository friendRepository,
		final Member loginMember,
		final long friendId
	) {
		return friendRepository.findFriendByToId(loginMember, friendId)
			.orElseThrow(() -> new SignatureException(MemberErrorCode.NOT_FOUND_MEMBER));
	}
}
