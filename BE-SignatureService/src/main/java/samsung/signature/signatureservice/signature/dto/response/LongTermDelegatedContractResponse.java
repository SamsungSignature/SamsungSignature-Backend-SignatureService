package samsung.signature.signatureservice.signature.dto.response;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import samsung.signature.signatureservice.signature.dto.request.SignatureConstraint;

@Builder
public record LongTermDelegatedContractResponse(
	@JsonProperty("to")
	List<ToInfo> to,
	@JsonProperty("limit_amount")
	Integer limitAmount,
	@JsonProperty("limit_date")
	String limitPeriod,
	@JsonProperty("card")
	ToInfo cardInfo
) {
	public static LongTermDelegatedContractResponse of(
		final List<ToMemberInfo> friends,
		final SignatureConstraint signatureConstraint,
		final CardInfoToken cardInfoToken
	) {
		return LongTermDelegatedContractResponse.builder()
			.to(friends.stream()
				.map(friend -> ToInfo.builder()
					.id(friend.member().getId())
					.name(friend.name())
					.build())
				.toList())
			.cardInfo(ToInfo.builder()
				.id(cardInfoToken.cardId())
				.name(cardInfoToken.cardName())
				.build()
			)
			.limitAmount(signatureConstraint.getLimitAmount())
			.limitPeriod(signatureConstraint.getLimitDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
			.build();
	}

	@Builder
	static class ToInfo {
		@JsonProperty("id")
		private Long id;
		@JsonProperty("name")
		private String name;
	}
}
