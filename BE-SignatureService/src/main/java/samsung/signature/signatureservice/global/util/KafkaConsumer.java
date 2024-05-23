package samsung.signature.signatureservice.global.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import samsung.signature.common.exception.SignatureException;
import samsung.signature.common.utils.RedisUtils;
import samsung.signature.signatureservice.global.dto.kafka.PickCardEvent;
import samsung.signature.signatureservice.notification.domain.NotificationType;
import samsung.signature.signatureservice.notification.service.NotificationService;
import samsung.signature.signatureservice.signature.domain.SignatureDetail;
import samsung.signature.signatureservice.signature.exception.SignatureDetailErrorCode;
import samsung.signature.signatureservice.signature.repository.SignatureDetailRepository;
import samsung.signature.signatureservice.signature.util.SignatureServiceUtil;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {
	private final NotificationService notificationService;
	private final SignatureDetailRepository signatureDetailRepository;
	private final RedisTemplate<byte[], byte[]> redisTemplate;
	private final ObjectMapper objectMapper;
	private static final String SIGNATURE_STATUS_PREFIX = "signature-status:";

	@Transactional(readOnly = true)
	@KafkaListener(topics = "wallet-signature-pickcard-topic", groupId = "signature")
	public void pickCard(String kafkaEvent) {
		try {
			PickCardEvent event = objectMapper.readValue(kafkaEvent, PickCardEvent.class);

			SignatureDetail signatureDetail = SignatureServiceUtil.getSignatureDetail(
				signatureDetailRepository, event.getSignatureDetailId()
			);
			notificationService.publish(signatureDetail.getFrom().getId(), NotificationType.INPROGRESS_PICKCARD,
				signatureDetail);
			RedisUtils.put(
				redisTemplate,
				objectMapper,
				NotificationType.INPROGRESS_PICKCARD,
				SIGNATURE_STATUS_PREFIX, signatureDetail.getId()
			);
		} catch (JsonProcessingException e) {
			throw new SignatureException(SignatureDetailErrorCode.NOT_FOUND_SIGNATURE_DETAIL);
		}
	}
}
