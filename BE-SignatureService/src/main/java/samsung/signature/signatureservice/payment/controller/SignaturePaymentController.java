package samsung.signature.signatureservice.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import samsung.signature.common.response.MessageBody;
import samsung.signature.common.response.ResponseEntityFactory;
import samsung.signature.signatureservice.payment.service.SignaturePaymentService;

@RequiredArgsConstructor
@RequestMapping("/signature-service")
@RestController
public class SignaturePaymentController {
	private final SignaturePaymentService signaturePaymentService;

	@PostMapping("/v1/payments/{signature_detail_card_id}")
	public ResponseEntity<MessageBody<Void>> putInSignatureToken(
		@RequestHeader(name = "Member-Id", required = true) Long memberId,
		@PathVariable(name = "signature_detail_card_id") Long signatureDetailCardId
	) {
		signaturePaymentService.signatureTokenPut(memberId, signatureDetailCardId);
		return ResponseEntityFactory.ok(
			"대리 결제 준비를 성공적으로 처리하였습니다."
		);
	}
}
