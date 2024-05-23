package samsung.signature.signatureservice.signature.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import samsung.signature.signatureservice.signature.domain.ValidateType;

@Builder
public record GivenCardDetailResponse(
	@JsonProperty("given_card_id") Long givenCardId,
	@JsonProperty("card_username") String cardUsername,
	@JsonProperty("validate_type") ValidateType validateType
) {
}
