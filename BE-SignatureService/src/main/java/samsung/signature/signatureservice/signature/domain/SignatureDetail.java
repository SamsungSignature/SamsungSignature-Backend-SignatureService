package samsung.signature.signatureservice.signature.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import samsung.signature.signatureservice.global.domain.BaseTime;
import samsung.signature.signatureservice.member.domain.Member;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "signature_details_tbl")
public class SignatureDetail extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "signature_detail_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "from_member_id")
	private Member from;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "to_member_id")
	private Member to;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "signature_detail_card_id")
	private SignatureDetailCard signatureDetailCard;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "condition_id")
	private Condition condition;

	@Enumerated(EnumType.STRING)
	@Column(name = "permission_status")
	private PermissionStatus permissionStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "signature_type")
	private SignatureType signatureType;

	public static SignatureDetail of(
		final Member from,
		final Member to,
		final Condition condition,
		final SignatureType signatureType,
		final PermissionStatus permissionStatus
	) {
		return SignatureDetail.builder()
			.from(from)
			.to(to)
			.condition(condition)
			.signatureType(signatureType)
			.permissionStatus(permissionStatus)
			.build();
	}

	public SignatureDetail updateProcess(final PermissionStatus permissionStatus) {
		this.permissionStatus = permissionStatus;
		return this;
	}

	public void addSignatureDetailCard(final SignatureDetailCard signatureDetailCard) {
		this.signatureDetailCard = signatureDetailCard;
	}
}
