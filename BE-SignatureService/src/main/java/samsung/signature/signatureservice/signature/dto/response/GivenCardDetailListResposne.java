package samsung.signature.signatureservice.signature.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record GivenCardDetailListResposne(
	@JsonProperty("given_card_detail_list") List<GivenCardDetailResponse> givenCardDetailResponseList
) {
}
