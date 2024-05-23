package samsung.signature.signatureservice.signature.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CardModifiedInfo(
	@JsonProperty("card_nickname")
	String cardNickName
) {
}
