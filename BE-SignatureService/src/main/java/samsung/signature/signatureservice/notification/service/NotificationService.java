package samsung.signature.signatureservice.notification.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import samsung.signature.signatureservice.notification.domain.Notification;
import samsung.signature.signatureservice.notification.domain.NotificationType;
import samsung.signature.signatureservice.notification.dto.response.NotificationListResponse;
import samsung.signature.signatureservice.notification.dto.response.NotificationResponse;
import samsung.signature.signatureservice.notification.repository.EmitterRepository;
import samsung.signature.signatureservice.notification.repository.NotificationRepository;
import samsung.signature.signatureservice.notification.util.NotificationMessageUtil;
import samsung.signature.signatureservice.signature.domain.SignatureDetail;
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class NotificationService {
	private final EmitterRepository emitterRepository;
	private final NotificationRepository notificationRepository;

	public NotificationListResponse getNotifications(long memberId) {
		List<NotificationResponse> notificationList = notificationRepository.findAllByMemberId(memberId)
			.stream()
			.map(NotificationResponse::from)
			.toList();
		return NotificationListResponse.from(notificationList);
	}

	@Transactional
	public SseEmitter subscribe(long memberId) throws IOException {
		return emitterRepository.save(memberId);
	}

	@Transactional
	public void publish(long memberId, NotificationType notificationType, SignatureDetail signatureDetail) {
		SseEmitter emitter = emitterRepository.findById(memberId);
		if (emitter != null) {
			try {
				String message = NotificationMessageUtil.generateMessage(notificationType, signatureDetail);
				emitter.send(
					SseEmitter.event()
						.data(NotificationResponse.of(signatureDetail, notificationType, message))
				);
				if (!(notificationType == NotificationType.INPROGRESS_CONSIDER
					|| notificationType == NotificationType.INPROGRESS_PICKCARD
				)) {
					notificationRepository.save(
						Notification.of(signatureDetail, notificationType, message)
					);
				}
			} catch (Exception e) {
				emitterRepository.deleteById(memberId);
				log.info("SSE EXCEPTION : {} " , e.getMessage());
			}
		}
	}

	@Transactional
	public void publishPayment(long memberId, NotificationType notificationType, SignatureDetail signatureDetail,
		int price) {
		SseEmitter emitter = emitterRepository.findById(memberId);
		if (emitter != null) {
			try {
				String message = "시그니처 앱카드 " + price + "원 결제";
				emitter.send(
					SseEmitter.event()
						.data(NotificationResponse.of(signatureDetail, notificationType, message))
				);
				notificationRepository.save(
					Notification.of(signatureDetail, notificationType, message)
				);
			} catch (Exception e) {
				log.info("SSE EXCEPTION : {} ", e.getMessage());
				emitterRepository.deleteById(memberId);
			}
		}
	}
}
