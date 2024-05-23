package samsung.signature.signatureservice.payment.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import samsung.signature.signatureservice.payment.dto.request.SignaturePaymentInfo;
import samsung.signature.signatureservice.payment.service.SignaturePaymentService;

@RequiredArgsConstructor
@RequestMapping("/signature-service")
@RestController
public class WalletController {
	private final SignaturePaymentService signaturePaymentService;

	@PostMapping("/v1/payments")
	public String useSignatureCard(
		@RequestBody SignaturePaymentInfo signaturePaymentInfo
	) {
		return signaturePaymentService.useSignatureCard(signaturePaymentInfo);
	}
}
