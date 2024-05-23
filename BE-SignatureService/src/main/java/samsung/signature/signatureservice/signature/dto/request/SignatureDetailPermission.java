package samsung.signature.signatureservice.signature.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import samsung.signature.signatureservice.signature.domain.PermissionStatus;

public record SignatureDetailPermission(
	@JsonProperty("permission_type")
	String permission,
	@JsonProperty("card_id")
	Long cardId
) {
	public PermissionStatus getPermission() {
		return PermissionStatus.valueOf(permission);
	}
}
