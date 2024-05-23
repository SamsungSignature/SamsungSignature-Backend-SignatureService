package samsung.signature.signatureservice.notification.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import samsung.signature.common.response.MessageBody;
import samsung.signature.common.response.ResponseEntityFactory;
import samsung.signature.signatureservice.notification.dto.response.NotificationListResponse;
import samsung.signature.signatureservice.notification.service.NotificationService;

@RequiredArgsConstructor
@RequestMapping("/signature-service")
@RestController
public class NotificationController {
	private final NotificationService notificationService;

	@GetMapping("/v1/notifications")
	public ResponseEntity<MessageBody<NotificationListResponse>> getNotifications(
		@RequestHeader(name = "Member-Id", required = true) final long memberId
	){
		return ResponseEntityFactory.ok(
			"알림목록을 성공적으로 조회하였습니다.",
			notificationService.getNotifications(memberId)
		);
	}

	@GetMapping("/v1/notifications/subscribe")
	public SseEmitter subscribe(
		@RequestHeader(name = "Member-Id", required = true) final long memberId,
		HttpServletResponse response
	) throws IOException {
		// NGINX REVERSE PLOXY PROTECT BUFFERING
		response.setHeader("X-Accel-Buffering", "no");
		return notificationService.subscribe(memberId);
	}
}
