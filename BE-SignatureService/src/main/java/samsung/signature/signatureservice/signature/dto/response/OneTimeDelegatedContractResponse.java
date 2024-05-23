package samsung.signature.signatureservice.signature.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import samsung.signature.signatureservice.signature.dto.request.OneTimeSignatureConstraint;

@Builder
public record OneTimeDelegatedContractResponse(
	@JsonProperty("to_id")
	Long toId,
	@JsonProperty("to_name")
	String toName,
	@JsonProperty("limit_amount")
	Integer limitAmount,
	@JsonProperty("constraints")
	OneTimeConstraints constraints
) {
	public static OneTimeDelegatedContractResponse of(
		final ToMemberInfo friend,
		final OneTimeSignatureConstraint delegatedContractRequest
	) {
		return OneTimeDelegatedContractResponse.builder()
			.toId(friend.member().getId())
			.toName(friend.name())
			.limitAmount(delegatedContractRequest.limitAmount())
			.constraints(OneTimeConstraints.builder()
				.marketName(delegatedContractRequest.marketName())
				.item(delegatedContractRequest.item())
				.itemImage(delegatedContractRequest.itemImage())
				.build()
			)
			.build();
	}

	@Builder
	static class OneTimeConstraints {
		@JsonProperty("market_name")
		String marketName;
		@JsonProperty("item")
		String item;
		@JsonProperty("item_image")
		String itemImage;
	}
}
