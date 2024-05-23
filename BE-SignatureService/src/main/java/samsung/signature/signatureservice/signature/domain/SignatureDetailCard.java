package samsung.signature.signatureservice.signature.domain;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import samsung.signature.signatureservice.global.domain.BaseTime;
import samsung.signature.signatureservice.signature.dto.response.CardInfoToken;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "signature_detail_cards_tbl")
public class SignatureDetailCard extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "signature_detail_card_id")
	private Long id;

	@Column(name = "card_id")
	private Long cardId;

	@Enumerated(EnumType.STRING)
	@Column(name = "is_validate")
	private ValidateType isValidate;

	@Column(name = "transaction_card_nickname")
	private String nickname;

	@Column(name = "is_deleted")
	@ColumnDefault("false")
	private Boolean isDeleted;

	public static SignatureDetailCard of(
		final SignatureDetail signatureDetail,
		final CardInfoToken cardInfoToken
	) {
		SignatureDetailCard signatureDetailCard = SignatureDetailCard.builder()
			.cardId(cardInfoToken.cardId())
			.isValidate(ValidateType.ON)
			.nickname(cardInfoToken.cardName())
			.isDeleted(Boolean.FALSE)
			.build();
		signatureDetail.addSignatureDetailCard(signatureDetailCard);
		return signatureDetailCard;
	}

	public void exchangeIsValidate(ValidateType type) {
		ValidateType changeType;
		if (type == ValidateType.ON) {
			changeType = ValidateType.OFF;
		} else {
			changeType = ValidateType.ON;
		}
		this.isValidate = changeType;
	}

	public void softDelete() {
		this.isDeleted = Boolean.TRUE;
	}

	public void modifyCardNickName(final String nickname) {
		this.nickname = nickname;
	}

	public void unValidate() {
		this.isValidate = ValidateType.OFF;
	}
}
