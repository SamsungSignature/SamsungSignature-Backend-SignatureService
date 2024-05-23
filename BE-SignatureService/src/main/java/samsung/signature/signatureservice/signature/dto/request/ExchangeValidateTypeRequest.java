package samsung.signature.signatureservice.signature.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import samsung.signature.signatureservice.signature.domain.ValidateType;

@Builder
public record ExchangeValidateTypeRequest(
	@JsonProperty("validate_type") ValidateType validateType
) {
}
