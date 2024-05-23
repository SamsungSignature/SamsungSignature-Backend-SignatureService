package samsung.signature.signatureservice.signature.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LongTermSignatureConstraint(
	@JsonProperty("to_ids")
	List<Long> toIds,
	@JsonProperty("limit_amount")
	Integer limitAmount,
	@JsonProperty("limit_date")
	String limitDate,
	@JsonProperty("card_id")
	Long cardId
) implements SignatureConstraint {
	@Override
	public Integer getLimitAmount() {
		return limitAmount();
	}

	@Override
	public LocalDate getLimitDate() {
		return LocalDate.parse(limitDate());
	}

	@Override
	public String getMarketName() {
		return null;
	}

	@Override
	public String getItem() {
		return null;
	}

	@Override
	public String getItemImage() {
		return null;
	}

	@Override
	public long getCardId() {
		return cardId();
	}
}
