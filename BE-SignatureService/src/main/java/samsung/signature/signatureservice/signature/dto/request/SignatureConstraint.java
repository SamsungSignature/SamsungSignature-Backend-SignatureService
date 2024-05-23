package samsung.signature.signatureservice.signature.dto.request;

import java.time.LocalDate;

public interface SignatureConstraint {
	public Integer getLimitAmount();
	public LocalDate getLimitDate();
	public String getMarketName();
	public String getItem();
	public String getItemImage();
	public long getCardId();
}
