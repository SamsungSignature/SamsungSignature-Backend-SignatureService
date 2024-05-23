package samsung.signature.signatureservice.signature.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record SignatureHistoryListResponse(
	@JsonProperty("signature_history_list") List<SignatureHistoryResponse> historyList
) {
}
