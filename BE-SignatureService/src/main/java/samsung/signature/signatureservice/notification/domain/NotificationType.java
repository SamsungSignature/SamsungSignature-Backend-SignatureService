package samsung.signature.signatureservice.notification.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public enum NotificationType {
	INPROGRESS_SUBMIT("요청 접수"),
	INPROGRESS_CONSIDER("요청 확인"),
	INPROGRESS_PICKCARD("카드 선택"),
	PAYMENT("결제"),
	CARD_EXPIRED("카드 만료"),
	REQUEST_EXPIRED("요청 만료"),
	APPROVED("요청 승인"),
	REJECTED("요청 거절"),
	ISSUED("카드 발급"),
	OFFON("권한 부여"),
	ONOFF("권한 해제"),
	;
	private final String value;
}