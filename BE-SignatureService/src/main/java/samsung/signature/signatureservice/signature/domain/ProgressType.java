package samsung.signature.signatureservice.signature.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProgressType {
	INPROGRESS_SUBMIT("요청 접수"),
	INPROGRESS_CONSIDER("요청 확인"),
	INPROGRESS_PICKCARD("카드 선택")
	;
	private final String value;
}
