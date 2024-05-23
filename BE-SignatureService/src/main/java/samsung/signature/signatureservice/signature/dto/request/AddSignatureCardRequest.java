package samsung.signature.signatureservice.signature.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record AddSignatureCardRequest(
	@JsonProperty("card_img") String cardImg
) {
}
