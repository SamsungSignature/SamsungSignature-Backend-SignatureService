package samsung.signature.signatureservice.signature.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record GivenCard(
	@JsonProperty("card_id") Long id,
	@JsonProperty("card_img") String cardImg,
	@JsonProperty("card_name") String name
) {
}
