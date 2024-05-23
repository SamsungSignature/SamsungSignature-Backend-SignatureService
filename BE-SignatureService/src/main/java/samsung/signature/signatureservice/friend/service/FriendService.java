package samsung.signature.signatureservice.friend.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import samsung.signature.signatureservice.friend.domain.Friend;
import samsung.signature.signatureservice.friend.dto.request.PhoneNumberList;
import samsung.signature.signatureservice.friend.dto.response.SignatureFriend;
import samsung.signature.signatureservice.friend.dto.response.SignatureFriends;
import samsung.signature.signatureservice.friend.repository.FriendRepository;
import samsung.signature.signatureservice.member.domain.Member;
import samsung.signature.signatureservice.member.repository.MemberRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FriendService {
	private final MemberRepository memberRepository;
	private final FriendRepository friendRepository;

	private static Set<Member> getNewFriends(
		final Set<Member> signatureFriends,
		final Set<Member> oldFriends
	) {
		Set<Member> diff = new HashSet<>(signatureFriends);
		diff.removeAll(oldFriends);
		return diff;
	}

	@Transactional
	public SignatureFriends syncFriendsInfo(
		final long memberId,
		final PhoneNumberList phoneNumberList
	) {
		Member loginMember = memberRepository.getReferenceById(memberId);
		Map<String, PhoneNumberList.FriendInfos> phoneBook = mapPhoneNumbersToFriendInfos(phoneNumberList);
		Set<Member> signatureMembers = new HashSet<>(
			memberRepository.findAllFriendsByPhoneNumbers(phoneBook.keySet())
		);

		updateFriendRelations(loginMember, signatureMembers, phoneBook);

		List<SignatureFriend> syncFriends = friendRepository.findAllFriends(loginMember);
		return SignatureFriends.from(syncFriends);
	}

	private Map<String, PhoneNumberList.FriendInfos> mapPhoneNumbersToFriendInfos(
		final PhoneNumberList phoneNumberList
	) {
		return phoneNumberList.friendInfos().stream()
			.collect(Collectors.toMap(
				PhoneNumberList.FriendInfos::getPhoneNumber,
				Function.identity(),
				(existing, replacement) -> replacement)
			);
	}

	private void updateFriendRelations(
		final Member loginMember,
		final Set<Member> signatureMembers,
		final Map<String, PhoneNumberList.FriendInfos> phoneBook
	) {
		Set<Member> oldFriends = friendRepository.findAllFriendIdsSetByFromId(loginMember);
		Set<Member> newFriends = getNewFriends(signatureMembers, oldFriends);
		addNewFriends(phoneBook, newFriends, loginMember);
	}

	private void addNewFriends(
		final Map<String, PhoneNumberList.FriendInfos> phoneBook,
		final Set<Member> newFriendsMember,
		final Member loginMember
	) {
		Set<Friend> newFriends = newFriendsMember.stream()
			.map(friend -> createFriend(loginMember, friend, phoneBook))
			.collect(Collectors.toSet());
		friendRepository.saveAll(newFriends);
	}

	private Friend createFriend(
		final Member loginMember,
		final Member friend,
		final Map<String, PhoneNumberList.FriendInfos> phoneBook
	) {
		PhoneNumberList.FriendInfos friendInfo = phoneBook.get(friend.getPhoneNumber());
		String friendName = friendInfo.getDisplayName();
		return Friend.of(loginMember, friend, friendName);
	}
}
