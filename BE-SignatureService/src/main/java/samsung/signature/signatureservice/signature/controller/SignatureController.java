package samsung.signature.signatureservice.signature.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import samsung.signature.common.response.MessageBody;
import samsung.signature.common.response.ResponseEntityFactory;
import samsung.signature.signatureservice.signature.dto.request.AddSignatureCardRequest;
import samsung.signature.signatureservice.signature.dto.response.ExistsSignatureCardResponse;
import samsung.signature.signatureservice.signature.dto.response.SignatureHistoryDetailResponse;
import samsung.signature.signatureservice.signature.dto.request.ExchangeValidateTypeRequest;
import samsung.signature.signatureservice.signature.dto.response.SignatureHistoryListResponse;
import samsung.signature.signatureservice.signature.service.SignatureService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/signature-service")
@Slf4j
public class SignatureController {
	private final SignatureService signatureService;

	@GetMapping("/v1/signatures/history")
	public ResponseEntity<MessageBody<SignatureHistoryListResponse>> getHistories(
		@RequestHeader(name = "Member-Id", required = true) Long memberId,
		@RequestParam("type") String type
	) {
		return ResponseEntityFactory.ok(
			"시그니처 내역 조회 성공",
			signatureService.getSignatureHistoryList(type, memberId)
		);
	}

	@GetMapping("/v1/signatures/history/{signature_detail_id}")
	public ResponseEntity<MessageBody<SignatureHistoryDetailResponse>> getHistoryDetail(
		@RequestHeader(name = "Member-Id", required = true) Long memberId,
		@RequestParam("type") String type,
		@PathVariable(name = "signature_detail_id") Long signatureDetailId) {

		return ResponseEntityFactory.ok(
			type
				+ " 상세 내역 조회 성공",
			signatureService.getSignatureDetail(memberId, type, signatureDetailId)
		);
	}

	@PatchMapping("/v1/given-cards/{given_card_id}")
	public ResponseEntity<MessageBody<Void>> exchangeValidateType(
		@PathVariable(name = "given_card_id") Long signatureDetailCardId,
		@RequestBody ExchangeValidateTypeRequest request) {
		signatureService.exchangeValidateType(signatureDetailCardId, request.validateType());
		return ResponseEntityFactory.ok("카드 승인 여부가 변경되었습니다.");
	}

	@DeleteMapping("/v1/given-cards/{given_card_id}")
	public ResponseEntity<MessageBody<Void>> deleteApproval(
		@PathVariable(name = "given_card_id") Long signatureDetailCardId) {
		signatureService.deleteApproval(signatureDetailCardId);
		return ResponseEntityFactory.ok("빌려준 카드를 성공적으로 삭제했습니다. (권한 부여 취소)");
	}

	@PostMapping("/v1/signature-cards")
	public ResponseEntity<MessageBody<Void>> addSignatureCard(
		@RequestHeader(name = "Member-Id", required = true) Long memberId,
		@RequestBody AddSignatureCardRequest request) {
		signatureService.addSignatureCard(memberId, request);

		return ResponseEntityFactory.ok("wallet 앱 카드 등록 완료");
	}

	@GetMapping("/v1/signature")
	public ResponseEntity<MessageBody<ExistsSignatureCardResponse>> isExistsSignatureCard(
		@RequestHeader(name = "Member-Id", required = true) Long memberId
	) {
		return ResponseEntityFactory.ok("시그니처 카드 유무 조회 성공", signatureService.isExistsSignatureCard(memberId));
	}

}
