package samsung.signature.signatureservice.notification.repository;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import samsung.signature.signatureservice.notification.dto.response.NotificationResponse;

@Repository
public class EmitterRepository {
    private static final Long DEFAULT_TIMEOUT = 600L * 1000 * 60;

	private final Map<Long, SseEmitter> emitterList = new ConcurrentHashMap<>();

	public SseEmitter save(Long memberId) throws IOException {
		SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
		emitterList.put(memberId, emitter);
		emitter.onCompletion(() -> emitterList.remove(memberId));
		emitter.onTimeout(() -> emitterList.remove(memberId));
		emitter.onError((callback) -> emitterList.remove(memberId));
		emitter.send(NotificationResponse.builder()
			.message("SSE CONNECTED")
			.build());
		return emitter;
	}

	public void deleteById(Long memberId) {
        emitterList.remove(memberId);
    }

	public SseEmitter findById(Long memberId) {
		return emitterList.get(memberId);
	}
}
