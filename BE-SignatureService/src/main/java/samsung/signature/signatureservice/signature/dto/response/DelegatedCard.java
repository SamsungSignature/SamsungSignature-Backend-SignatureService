package samsung.signature.signatureservice.signature.dto.response;

import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryProjection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import samsung.signature.signatureservice.signature.domain.SignatureDetailCard;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DelegatedCard {
	@JsonProperty("signature_detail_id")
	private Long signatureDetailId;
	@JsonProperty("signature_detail_card_id")
	private Long signatureDetailCardId;
	@JsonProperty("id")
	private Long cardId;
	@JsonProperty("nickname")
	private String cardNickname;
	@JsonProperty("company")
	private String cardCompany;
	@JsonProperty("card_img")
	private String img;
	@JsonProperty("expired_in")
	private long expiredIn;
	@JsonProperty("card_owner")
	private String cardOwner;

	@QueryProjection
	@Builder
	public DelegatedCard(
		final Long signatureDetailId,
		final Long signatureDetailCardId,
		final Long cardId,
		final String cardNickname,
		final String cardOwner
	) {
		this.signatureDetailId = signatureDetailId;
		this.signatureDetailCardId = signatureDetailCardId;
		this.cardId = cardId;
		this.cardNickname = cardNickname;
		this.cardOwner = cardOwner;
	}

	public static DelegatedCard from(final SignatureDetailCard signatureDetailCard) {
		return DelegatedCard.builder()
			.signatureDetailId(signatureDetailCard.getId())
			.cardId(signatureDetailCard.getCardId())
			.cardNickname(signatureDetailCard.getNickname())
			.build();
	}

	public DelegatedCard update(final CardInfo cardInfo, final long ttl) {
		this.cardNickname = ObjectUtils.isEmpty(this.cardNickname) ? cardInfo.getCardName() : this.cardNickname;
		this.cardCompany = cardInfo.getCardCompany();
		this.img = cardInfo.getCardImg();
		this.expiredIn = ttl * 1000L + System.currentTimeMillis();
		return this;
	}
}
