package samsung.signature.signatureservice.global.dto.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class PickCardEvent {
	@JsonProperty("signature_detail_id")
	private Long signatureDetailId;
}