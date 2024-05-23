package samsung.signature.signatureservice.friend.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SignatureFriends(
	@JsonProperty("friends_list")
	List<SignatureFriend> friends
) {
	public static SignatureFriends from(List<SignatureFriend> friends) {
		return new SignatureFriends(friends);
	}
}

