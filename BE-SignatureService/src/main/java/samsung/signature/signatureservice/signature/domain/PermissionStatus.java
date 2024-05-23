package samsung.signature.signatureservice.signature.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public enum PermissionStatus {
	INPROGRESS("진행중"),
	APPROVED("승인"),
	REJECTED("거절"),
	;
	private final String value;
}
