package samsung.signature.signatureservice.friend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SignatureFriend {
	@JsonProperty("id")
	private long id;
	@JsonProperty("name")
	private String name;
	@JsonProperty("phone_number")
	private String phoneNumber;

	@QueryProjection
	public SignatureFriend(
		final Long id,
		final String friendName,
		final String phoneNumber
	) {
		this.id = id;
		this.name = friendName;
		this.phoneNumber = phoneNumber;
	}
}
