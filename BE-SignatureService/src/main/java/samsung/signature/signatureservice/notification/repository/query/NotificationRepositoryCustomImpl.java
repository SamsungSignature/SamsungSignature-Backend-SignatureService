package samsung.signature.signatureservice.notification.repository.query;

import static samsung.signature.signatureservice.notification.domain.QNotification.*;

import java.util.List;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import samsung.signature.signatureservice.notification.domain.Notification;
import samsung.signature.signatureservice.notification.domain.NotificationType;

@RequiredArgsConstructor
public class NotificationRepositoryCustomImpl implements NotificationRepositoryCustom{
	private final JPAQueryFactory queryFactory;

	@Override
	public List<Notification> findAllByMemberId(Long memberId) {
		return queryFactory.selectFrom(notification)
			.where(signatureRequest(memberId)
				.or(signatureApproved(memberId)
					.or(signatureRejected(memberId)
						.or(signatureIssued(memberId)
							.or(signatureOnoff(memberId)
								.or(signatureOffon(memberId)))))))
			.orderBy(notification.createdAt.desc())
			.fetch();
	}

	private BooleanExpression signatureRequest(Long memberId){
		return notification.to.id.eq(memberId)
			.and(notification.notificationType.eq(NotificationType.INPROGRESS_SUBMIT));
	}

	private BooleanExpression signatureApproved(Long memberId){
		return notification.from.id.eq(memberId)
			.and(notification.notificationType.eq(NotificationType.APPROVED));
	}

	private BooleanExpression signatureRejected(Long memberId){
		return notification.from.id.eq(memberId)
			.and(notification.notificationType.eq(NotificationType.REJECTED));
	}

	private BooleanExpression signatureIssued(Long memberId){
		return notification.from.id.eq(memberId)
			.and(notification.notificationType.eq(NotificationType.ISSUED));
	}

	private BooleanExpression signatureOnoff(Long memberId){
		return notification.from.id.eq(memberId)
			.and(notification.notificationType.eq(NotificationType.ONOFF));
	}

	private BooleanExpression signatureOffon(Long memberId){
		return notification.from.id.eq(memberId)
			.and(notification.notificationType.eq(NotificationType.OFFON));
	}
}
