package samsung.signature.signatureservice.friend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import samsung.signature.common.response.MessageBody;
import samsung.signature.common.response.ResponseEntityFactory;
import samsung.signature.signatureservice.friend.dto.request.PhoneNumberList;
import samsung.signature.signatureservice.friend.dto.response.SignatureFriends;
import samsung.signature.signatureservice.friend.service.FriendService;

@RequiredArgsConstructor
@RequestMapping("/signature-service")
@RestController
public class FriendController {
	private final FriendService friendService;

	@PostMapping("/v1/friends")
	public ResponseEntity<MessageBody<SignatureFriends>> syncFriend(
		@RequestHeader(name = "Member-Id", required = true) final long memberId,
		@RequestBody final PhoneNumberList phoneNumberList) {
		return ResponseEntityFactory.ok(
			"친구목록이 성공적으로 동기화되었습니다.",
			friendService.syncFriendsInfo(memberId, phoneNumberList)
		);
	}

}
