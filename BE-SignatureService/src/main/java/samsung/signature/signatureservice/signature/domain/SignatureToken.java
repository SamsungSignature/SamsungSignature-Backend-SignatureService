package samsung.signature.signatureservice.signature.domain;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import samsung.signature.signatureservice.signature.dto.request.SignatureConstraint;
import samsung.signature.signatureservice.signature.dto.response.CardInfoToken;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Builder
@Getter
public class SignatureToken implements Serializable {
	private String cardToken;
	private Integer limitAmount;

	public static SignatureToken of(
		final CardInfoToken cardInfoToken,
		final SignatureConstraint signatureConstraint
	) {
		return SignatureToken.builder()
			.cardToken(cardInfoToken.cardToken())
			.limitAmount(signatureConstraint.getLimitAmount())
			.build();
	}

	public SignatureToken updateCardToken(String token) {
		this.cardToken = token;
		return this;
	}
}
