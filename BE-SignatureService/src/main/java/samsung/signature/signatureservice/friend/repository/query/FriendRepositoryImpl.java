package samsung.signature.signatureservice.friend.repository.query;

import static samsung.signature.signatureservice.friend.domain.QFriend.*;
import static samsung.signature.signatureservice.member.domain.QMember.*;

import java.util.List;
import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import samsung.signature.signatureservice.friend.dto.response.QSignatureFriend;
import samsung.signature.signatureservice.friend.dto.response.SignatureFriend;
import samsung.signature.signatureservice.member.domain.Member;
import samsung.signature.signatureservice.signature.dto.response.QToMemberInfo;
import samsung.signature.signatureservice.signature.dto.response.ToMemberInfo;

@RequiredArgsConstructor
public class FriendRepositoryImpl implements FriendRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<SignatureFriend> findAllFriends(Member loginMember) {
		return queryFactory.select(new QSignatureFriend(
				member.id,
				friend.friendName,
				member.phoneNumber))
			.from(friend)
			.join(member)
			.on(member.eq(friend.to))
			.where(friend.from.eq(loginMember))
			.fetch();
	}

	@Override
	public Optional<ToMemberInfo> findFriendByToId(
		final Member loginMember,
		final Long friendId) {
		return Optional.ofNullable(
			queryFactory.select(new QToMemberInfo(
					friend.friendName,
					member
				))
				.from(friend)
				.join(friend.to, member)
				.where(friend.from.eq(loginMember)
					.and(friend.to.id.eq(friendId)))
				.fetchOne()
		);
	}

	@Override
	public List<ToMemberInfo> findAllFriendByToId(
		final Member loginMember,
		final List<Long> friendsId
	) {
		return queryFactory.select(new QToMemberInfo(
				friend.friendName,
				member)
			)
			.from(friend)
			.join(friend.to, member)
			.where(friend.from.eq(loginMember)
				.and(friend.to.id.in(friendsId)))
			.fetch();
	}
}
