package samsung.signature.signatureservice.signature.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import samsung.signature.common.response.MessageBody;
import samsung.signature.common.response.ResponseEntityFactory;
import samsung.signature.signatureservice.signature.dto.request.LongTermSignatureConstraint;
import samsung.signature.signatureservice.signature.dto.request.OneTimeSignatureConstraint;
import samsung.signature.signatureservice.signature.dto.request.SignatureDetailPermission;
import samsung.signature.signatureservice.signature.dto.response.LongTermDelegatedContractResponse;
import samsung.signature.signatureservice.signature.dto.response.OneTimeDelegatedContractResponse;
import samsung.signature.signatureservice.signature.dto.response.SignatureDetailPermissionResult;
import samsung.signature.signatureservice.signature.service.SignatureDelegatedCardService;

@RequiredArgsConstructor
@RequestMapping("/signature-service")
@RestController
public class SignatureDelegatedCardController {
	private final SignatureDelegatedCardService signatureDelegatedCardService;

	@PostMapping("/v1/delegated-cards/one-time")
	public ResponseEntity<MessageBody<OneTimeDelegatedContractResponse>> oneTimeDelegatedCard(
		@RequestHeader("Member-Id") final Long memberId,
		@RequestBody final OneTimeSignatureConstraint delegatedContractRequest
	) {
		return ResponseEntityFactory.created(
			"일회성 대리 결제가 성공적으로 요청되었습니다.",
			signatureDelegatedCardService.sendOneTimeDelegatedContract(
				memberId,
				delegatedContractRequest
			));
	}

	@PostMapping("/v1/delegated-cards/long-term")
	public ResponseEntity<MessageBody<LongTermDelegatedContractResponse>> longTermDelegatedCard(
		@RequestHeader("Member-Id") final Long memberId,
		@RequestBody final LongTermSignatureConstraint delegatedContractRequest
	) {
		return ResponseEntityFactory.created(
			"기간제 대리 결제가 성공적으로 수여되었습니다.",
			signatureDelegatedCardService.sendLongTermDelegatedContract(
				memberId,
				delegatedContractRequest
			));
	}

	@PatchMapping("/v1/delegated-cards/{signature_detail_id}/permission")
	public ResponseEntity<MessageBody<SignatureDetailPermissionResult>> changeDelegatedCardPermission(
		@RequestHeader("Member-Id") final Long memberId,
		@PathVariable("signature_detail_id") final Long signatureDetailId,
		@RequestBody final SignatureDetailPermission signatureDetailPermission
	) {
		SignatureDetailPermissionResult signatureDetailPermissionResult =
			signatureDelegatedCardService.changeDelegatedCardPermission(memberId, signatureDetailId,
				signatureDetailPermission);
		return ResponseEntityFactory.ok(
			"대리 결제 요청이 성공적으로 "
				+ signatureDetailPermissionResult.printMessage()
				+ " 처리되었습니다.",
			signatureDetailPermissionResult
		);
	}
}
