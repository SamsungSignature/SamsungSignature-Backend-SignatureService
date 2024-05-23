package samsung.signature.signatureservice.signature.dto.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;

public record OneTimeSignatureConstraint(
	@Valid
	@JsonProperty("to_id")
	Long toId,
	@Valid
	@JsonProperty("limit_amount")
	Integer limitAmount,
	@JsonProperty("market_name")
	String marketName,
	@JsonProperty("item")
	String item,
	@JsonProperty("item_image")
	String itemImage
) implements SignatureConstraint {
	@Override
	public Integer getLimitAmount() {
		return limitAmount();
	}

	@Override
	public LocalDate getLimitDate() {
		return null;
	}

	@Override
	public String getMarketName() {
		return marketName();
	}

	@Override
	public String getItem() {
		return item();
	}

	@Override
	public String getItemImage() {
		return itemImage();
	}

	@Override
	public long getCardId() {
		return 0L;
	}
}
