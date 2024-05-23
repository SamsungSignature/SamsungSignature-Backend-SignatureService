package samsung.signature.signatureservice.notification.repository.query;

import java.util.List;

import samsung.signature.signatureservice.notification.domain.Notification;

public interface NotificationRepositoryCustom {
	List<Notification> findAllByMemberId(Long memberId);
}
