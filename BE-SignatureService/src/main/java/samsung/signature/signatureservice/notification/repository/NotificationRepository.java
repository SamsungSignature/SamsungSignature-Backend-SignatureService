package samsung.signature.signatureservice.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import samsung.signature.signatureservice.notification.domain.Notification;
import samsung.signature.signatureservice.notification.repository.query.NotificationRepositoryCustom;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {
}
