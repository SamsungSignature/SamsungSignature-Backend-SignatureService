package samsung.signature.signatureservice.signature.domain;

import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import samsung.signature.signatureservice.global.domain.BaseTime;
import samsung.signature.signatureservice.signature.dto.request.SignatureConstraint;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "conditions_tbl")
public class Condition extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "condition_id")
	private Long id;

	@NotNull
	@Column(name = "limit_amount")
	private Integer limitAmount;

	@Column(name = "limit_date")
	private LocalDate limitDate;

	@Column(name = "market_name")
	private String marketName;

	@Column(name = "item")
	private String item;

	@Column(name = "item_image")
	private String itemImage;

	public static Condition from(
		final SignatureConstraint signatureDelegatedConstraint
	) {
		return Condition.builder()
			.limitDate(signatureDelegatedConstraint.getLimitDate())
			.limitAmount(signatureDelegatedConstraint.getLimitAmount())
			.marketName(signatureDelegatedConstraint.getMarketName())
			.item(signatureDelegatedConstraint.getItem())
			.itemImage(signatureDelegatedConstraint.getItemImage())
			.build();
	}
}
