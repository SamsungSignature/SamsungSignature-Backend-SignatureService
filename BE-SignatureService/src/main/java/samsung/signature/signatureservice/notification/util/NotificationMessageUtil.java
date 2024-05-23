package samsung.signature.signatureservice.notification.util;

import java.time.format.DateTimeFormatter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import samsung.signature.signatureservice.notification.domain.NotificationType;
import samsung.signature.signatureservice.signature.domain.Condition;
import samsung.signature.signatureservice.signature.domain.SignatureDetail;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationMessageUtil {
	public static String generateMessage(NotificationType notificationType, SignatureDetail signatureDetail) {
		switch (notificationType) {
			case INPROGRESS_SUBMIT: // "요청 접수"
				return signatureDetail.getFrom().getUserName() + " 님께서 대리결제를 요청하셨습니다.";
			case INPROGRESS_CONSIDER: // "요청 확인"
				return signatureDetail.getTo().getUserName() + " 님께서 대리결제를 확인하셨습니다.";
			case INPROGRESS_PICKCARD: //"카드 선택"
				return signatureDetail.getTo().getUserName() + " 님께서 카드를 선택하는 중입니다.";
			// case PAYMENT: // "결제"
			// 	return signatureDetail.getTo().getUserName() + " 님께서 대리결제를 완료하셨습니다.";
			case CARD_EXPIRED: // "카드 만료"
				return signatureDetail.getTo().getUserName() + " 님의 대리결제 카드가 만료되었습니다.";
			case REQUEST_EXPIRED: // "요청 만료"
				return signatureDetail.getTo().getUserName() + " 님에게 요청한 대리결제가 만료되었습니다.";
			case APPROVED: // "요청 승인"
				return signatureDetail.getTo().getUserName() + " 님께서 대리결제를 승인하였습니다.";
			case REJECTED: // "요청 거절"
				return signatureDetail.getTo().getUserName() + " 님께서 대리결제를 거절하였습니다.";
			case OFFON: // "권한 부여"
				return signatureDetail.getTo().getUserName() + " 님께서 대리결제 권한을 허용으로 갱신하였습니다.";
			case ONOFF: // "권한 해제"
				return signatureDetail.getTo().getUserName() + " 님께서 대리결제 권한을 해제으로 갱신하였습니다.";
			case ISSUED: { // "카드 발급"
				Condition condition = signatureDetail.getCondition();
				return signatureDetail.getTo().getUserName()
					+ " 님께서 "
					+ condition.getLimitDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
					+ "까지 대리결제 카드를 발급하였습니다.";
			}
			default:
				return "";
		}
	}
}
