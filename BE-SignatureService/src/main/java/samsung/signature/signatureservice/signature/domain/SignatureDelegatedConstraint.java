package samsung.signature.signatureservice.signature.domain;

import java.time.LocalDate;

import org.springframework.data.redis.core.RedisHash;

import jakarta.persistence.Id;
import lombok.Builder;
import samsung.signature.signatureservice.signature.dto.request.SignatureConstraint;

@Builder
@RedisHash(value = "signature_delegated_constraint", timeToLive = 86400) // 요청 대기시간 하루
public record SignatureDelegatedConstraint(
	@Id
	Long id,
	Integer limitAmount,
	LocalDate limitDate
) implements SignatureConstraint {
	public static SignatureDelegatedConstraint of(
		final SignatureDetail signatureDetail,
		final Condition condition) {
		return SignatureDelegatedConstraint.builder()
			.id(signatureDetail.getId())
			.limitAmount(condition.getLimitAmount())
			.limitDate(condition.getLimitDate())
			.build();
	}

	@Override
	public Integer getLimitAmount() {
		return limitAmount();
	}

	@Override
	public LocalDate getLimitDate() {
		return limitDate();
	}

	@Override
	public String getMarketName() {
		return null;
	}

	@Override
	public String getItem() {
		return null;
	}

	@Override
	public String getItemImage() {
		return null;
	}

	@Override
	public long getCardId() {
		return 0;
	}
}
