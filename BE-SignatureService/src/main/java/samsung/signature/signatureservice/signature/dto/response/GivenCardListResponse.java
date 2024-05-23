package samsung.signature.signatureservice.signature.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record GivenCardListResponse(
	@JsonProperty("given_card_list") List<GivenCard> givenCardList
) {
}
