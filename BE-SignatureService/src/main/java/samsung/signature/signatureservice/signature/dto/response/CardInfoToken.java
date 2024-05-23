package samsung.signature.signatureservice.signature.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryProjection;

public record CardInfoToken(
	@JsonProperty("card_id")
	Long cardId,
	@JsonProperty("card_name")
	String cardName,
	@JsonProperty("card_token")
	String cardToken
) {
	@QueryProjection
	public CardInfoToken(
		final Long cardId,
		final String cardName,
		final String cardToken
	) {
		this.cardId = cardId;
		this.cardName = cardName;
		this.cardToken = cardToken;
	}
}
