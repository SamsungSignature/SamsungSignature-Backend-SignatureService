package samsung.signature.signatureservice.signature.dto.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;
import samsung.signature.signatureservice.signature.domain.PermissionStatus;

@Builder
public record SignatureHistoryResponse(
	@JsonProperty("signature_detail_id") Long signatureDetailId,
	@JsonProperty("signature_name") String signatureName,
	@JsonProperty("permission_status") PermissionStatus permissionStatus,
	@JsonProperty("signature_date") String signatureDate,
	@JsonProperty("limit_amount") int limitAmount
) {
	@QueryProjection
	public SignatureHistoryResponse(
		Long signatureDetailId,
		final String signatureName,
		final PermissionStatus permissionStatus,
		final String signatureDate,
		final int limitAmount
	) {
		this.signatureDetailId = signatureDetailId;
		this.signatureName = signatureName;
		this.permissionStatus = permissionStatus;
		this.signatureDate = signatureDate;
		this.limitAmount = limitAmount;
	}
}
