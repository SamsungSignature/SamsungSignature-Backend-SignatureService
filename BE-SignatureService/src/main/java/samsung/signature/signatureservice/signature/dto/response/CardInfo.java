package samsung.signature.signatureservice.signature.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Builder
@Getter
public class CardInfo {
	@JsonProperty("card_id")
	private Long id;
	@JsonProperty("card_name")
	private String cardName;
	@JsonProperty("card_company")
	private String cardCompany;
	@JsonProperty("card_img")
	private String cardImg;

	@QueryProjection
	public CardInfo(
		final Long id,
		final String cardName,
		final String cardCompany,
		final String cardImg
	) {
		this.id = id;
		this.cardName = cardName;
		this.cardCompany = cardCompany;
		this.cardImg = cardImg;
	}
}
