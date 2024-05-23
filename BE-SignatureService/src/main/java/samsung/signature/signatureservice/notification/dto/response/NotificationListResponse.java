package samsung.signature.signatureservice.notification.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record NotificationListResponse(
	@JsonProperty("notifications")
	List<NotificationResponse> notificationList
) {
	public static NotificationListResponse from(List<NotificationResponse> notificationList) {
		return new NotificationListResponse(notificationList);
	}
}
