package samsung.signature.signatureservice.notification.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import samsung.signature.signatureservice.notification.domain.Notification;
import samsung.signature.signatureservice.notification.domain.NotificationType;
import samsung.signature.signatureservice.signature.domain.SignatureDetail;

@Builder
public record NotificationResponse(
	@JsonProperty("signature_id")
	Long signatureId,
	@JsonProperty("notification_type")
	NotificationType notificationType,
	@JsonProperty("message")
	String message,
	@JsonProperty("created_at")
	LocalDateTime createdAt
) {
	public static NotificationResponse from(final Notification notification) {
		return NotificationResponse.builder()
			.signatureId(notification.getSignatureId())
			.notificationType(notification.getNotificationType())
			.message(notification.getMessage())
			.createdAt(notification.getCreatedAt())
			.build();
	}

	public static NotificationResponse of(
		final SignatureDetail signatureDetail,
		final NotificationType notificationType,
		final String message
	) {
		return NotificationResponse.builder()
			.signatureId(signatureDetail.getId())
			.notificationType(notificationType)
			.message(message)
			.build();
	}
}