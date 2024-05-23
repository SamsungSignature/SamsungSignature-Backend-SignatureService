package samsung.signature.signatureservice.signature.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import samsung.signature.common.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum SignatureDetailErrorCode implements ErrorCode {
	NO_DELETE_CARD(404, "SIGNATURE_DETAIL_001", "삭제할 카드가 존재하지 않습니다."),
	NO_SIGNATURE_DETAIL_CARD(404, "SIGNATURE_DETAIL_002", "ON/OFF할 카드가 존재하지 않습니다."),
	NOT_EXISTS_TYPE(400, "SIGNATURE_DETAIL_003", "존재하지 않는 타입입니다."),
	NOT_FOUND_CARD(404, "SIGNATURE_DETAIL_004", "존재하지 않는 카드입니다."),
	NOT_IN_PROGRESS_SIGNATURE_DETAIL(400, "SIGNATURE_DETAIL_005", "진행중인 대리 결제 요청서가 아닙니다."),
	NOT_FOUND_SIGNATURE_DETAIL(404, "SIGNATURE_DETAIL_006", "존재하지 않는 대리 결제 요청서입니다."),
	CAN_NOT_ENCODE_SIGNATURE_TOKEN(400, "SIGNATURE_DETAIL_007", "서명 토큰을 생성할 수 없습니다."),
	EXIST_SIGNATURE_CARD(400, "CARD_002", "SIGNATURE 카드가 이미 존재합니다"),
	NOT_FOUND_SIGNATURE_DETAIL_HISTORY(400, "SIGNATURE_DETAIL_008", "SIGNATURE_DETAIL이 존재하지 않습니다."),
	NOT_VALIDATE_SIGNATURE_TOKEN(400, "SIGNATURE_DETAIL_009", "유효하지 않은 시그니처 토큰입니다."),
	OVER_LIMIT_PRICE(400, "SIGNATURE_DETAIL_010", "제한 금액을 초과한 결제 요청입니다."),
	;

	private final int statusCode;
	private final String errorCode;
	private final String message;

}
