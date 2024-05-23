package samsung.signature.signatureservice.global.config;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import samsung.signature.signatureservice.signature.service.SignatureExpirationHandlerService;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisEventListener implements MessageListener {
	private static final String SIGNATURE_REQUEST_KEY = "signature_delegated_constraint:";
	private static final String SIGNATURE_ACTIVE_TOKEN_KEY = "signature-active-token:";
	private static final String SIGNATURE_APPROVED_TOKEN_KEY = "signature-token:";
	private final SignatureExpirationHandlerService signatureExpirationHandlerService;

	private static long extractKey(String messageKey, String signatureApprovedTokenKey) {
		return Long.parseLong(messageKey.replace(signatureApprovedTokenKey, ""));
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		String messageKey = message.toString();
		log.info("Received message: {}", messageKey);
		if (messageKey.contains(SIGNATURE_REQUEST_KEY)) {
			// 카드 요청이 하루동안 승인나지 않았을 경우 자동 거절처리
			signatureExpirationHandlerService.handleRequestAfterOneDay(extractKey(messageKey, SIGNATURE_REQUEST_KEY));
		} else if (messageKey.contains(SIGNATURE_APPROVED_TOKEN_KEY)) {
			// 대리 결제 만료 기한이 지났을 경우 자동 비활성 처리
			signatureExpirationHandlerService.handleSignatureTokenAfterLimitDate(
				extractKey(messageKey, SIGNATURE_APPROVED_TOKEN_KEY));
		} else if (messageKey.contains(SIGNATURE_ACTIVE_TOKEN_KEY)) {
			// 활성화된 토큰이 잔존 시간을 넘었을 경우 자동 삭제 처리
			signatureExpirationHandlerService.handleActiveTokenAfterLimitMinute(
				extractKey(messageKey, SIGNATURE_ACTIVE_TOKEN_KEY));
		}
	}
}
