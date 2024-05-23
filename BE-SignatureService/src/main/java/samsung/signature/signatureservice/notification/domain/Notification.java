package samsung.signature.signatureservice.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import samsung.signature.signatureservice.global.domain.BaseTime;
import samsung.signature.signatureservice.member.domain.Member;
import samsung.signature.signatureservice.signature.domain.SignatureDetail;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notifications_tbl")
public class Notification extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "notification_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "from_member_id")
	private Member from;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "to_member_id")
	private Member to;

	@Column(name = "signature_id")
	private Long signatureId;

	@Enumerated(EnumType.STRING)
	@Column(name = "notification_type")
	private NotificationType notificationType;

	@Column(name = "message")
	private String message;

	public static Notification of(
		final SignatureDetail signatureDetail,
		final NotificationType notificationType,
		final String message
	) {
		return Notification.builder()
			.from(signatureDetail.getFrom())
			.to(signatureDetail.getTo())
			.signatureId(signatureDetail.getId())
			.notificationType(notificationType)
			.message(message)
			.build();
	}
}
