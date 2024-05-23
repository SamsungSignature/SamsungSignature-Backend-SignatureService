package samsung.signature.signatureservice.payment.domain;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import samsung.signature.signatureservice.signature.domain.SignatureTokenInfo;

@Builder
@RedisHash(value = "signature-active-token", timeToLive = 300)
@Getter
public class SignatureActiveToken {
	@Id
	private Long id;
	@Indexed
	private String token;
	private SignatureTokenInfo signatureTokenInfo;

	public static SignatureActiveToken from(
		final SignatureTokenInfo signatureTokenInfo
	){
		return SignatureActiveToken.builder()
			.id(signatureTokenInfo.getId())
			.token(signatureTokenInfo.getEncodedToken())
			.signatureTokenInfo(signatureTokenInfo)
			.build();
	}
}
