package samsung.signature.signatureservice.signature.domain;

import java.io.Serializable;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Accessors(chain = true)
@RedisHash("signature-token")
public class SignatureTokenInfo implements Serializable {
	@Id
	private Long id;
	private Long signatureDetailId;
	private String encodedToken;
	private String encodedSecretKey;
	private SignatureType signatureType;
	@TimeToLive
	private long ttl;
	public static SignatureTokenInfo of(
		final String encryptedSignatureToken,
		final String encryptedSecretKey
	) {
		return SignatureTokenInfo.builder()
			.encodedToken(encryptedSignatureToken)
			.encodedSecretKey(encryptedSecretKey)
			.build();
	}

	public SignatureTokenInfo updateTokenInfo(
		final SignatureDetail signatureDetail,
		final long ttl
	) {
		this.id = signatureDetail.getSignatureDetailCard().getId();
		this.signatureDetailId = signatureDetail.getId();
		this.signatureType = signatureDetail.getSignatureType();
		this.ttl = ttl;
		return this;
	}
}
