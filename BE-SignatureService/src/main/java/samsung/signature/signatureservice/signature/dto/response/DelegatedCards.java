package samsung.signature.signatureservice.signature.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record DelegatedCards(
	@JsonProperty("cards")
	List<DelegatedCard> delegatedCards
) {
	public static DelegatedCards from(final List<DelegatedCard> delegatedCards) {
		return DelegatedCards.builder()
			.delegatedCards(delegatedCards)
			.build();
	}
}
