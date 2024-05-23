package samsung.signature.signatureservice.member.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import samsung.signature.common.exception.ErrorCode;
@AllArgsConstructor
@Getter
public enum MemberErrorCode implements ErrorCode {
	NOT_FOUND_MEMBER(404, "해당하는 회원이 없습니다.", "MEMBER-004"),
	;
	private final int statusCode;
	private final String message;
	private final String errorCode;

}