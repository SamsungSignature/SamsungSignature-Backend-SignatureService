package samsung.signature.signatureservice.signature.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import samsung.signature.signatureservice.signature.domain.PermissionStatus;
import samsung.signature.signatureservice.signature.domain.SignatureDelegatedConstraint;
import samsung.signature.signatureservice.signature.domain.SignatureDetail;

@Builder
public record SignatureDetailPermissionResult(
	@JsonProperty("from_id")
	Long fromId,
	@JsonProperty("from_name")
	String fromName,
	@JsonProperty("limit_amount")
	Integer limitAmount,
	@JsonProperty("permission_type")
	PermissionStatus permissionType
) {
	public static SignatureDetailPermissionResult of(
		final SignatureDetail signatureDetail,
		final SignatureDelegatedConstraint signatureDelegatedConstraint
	) {
		return SignatureDetailPermissionResult.builder()
			.fromId(signatureDetail.getFrom().getId())
			.fromName(signatureDetail.getFrom().getUserName())
			.limitAmount(signatureDelegatedConstraint.limitAmount())
			.permissionType(signatureDetail.getPermissionStatus())
			.build();
	}

	public String printMessage() {
		return permissionType.getValue();
	}
}
