package samsung.signature.signatureservice.signature.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import samsung.signature.common.response.MessageBody;
import samsung.signature.common.response.ResponseEntityFactory;
import samsung.signature.signatureservice.signature.dto.request.CardModifiedInfo;
import samsung.signature.signatureservice.signature.dto.response.DelegatedCard;
import samsung.signature.signatureservice.signature.dto.response.DelegatedCards;
import samsung.signature.signatureservice.signature.dto.response.GivenCardDetailListResposne;
import samsung.signature.signatureservice.signature.dto.response.GivenCardListResponse;
import samsung.signature.signatureservice.signature.service.SignatureDetailCardService;

@RequiredArgsConstructor
@RequestMapping("/signature-service")
@RestController
public class SignatureDetailCardController {
	private final SignatureDetailCardService signatureDetailCardService;

	@DeleteMapping("/v1/delegated-cards/{card_id}")
	public ResponseEntity<MessageBody<Void>> deleteDelegatedCard(
		@RequestHeader("Member-Id") final Long memberId,
		@PathVariable("card_id") final Long cardId
	) {
		signatureDetailCardService.deleteDelegatedCard(memberId, cardId);
		return ResponseEntityFactory.status(
			HttpStatus.NO_CONTENT,
			"대리 결제 카드를 성공적으로 삭제하였습니다."
		);
	}

	@GetMapping("/v1/delegated-cards")
	public ResponseEntity<MessageBody<DelegatedCards>> getApprovedCards(
		@RequestHeader(name = "Member-Id", required = true) final Long memberId
	) {
		return ResponseEntityFactory.ok(
			"내가 받은 카드 목록이 성공적으로 조회되었습니다.",
			signatureDetailCardService.getApprovedCards(memberId)
		);
	}

	@PatchMapping("/v1/delegated-cards/{card_id}")
	public ResponseEntity<MessageBody<DelegatedCard>> modifyCardNickName(
		@RequestHeader(name = "Member-Id", required = true) final Long memberId,
		@PathVariable("card_id") final Long cardId,
		@RequestBody final CardModifiedInfo cardNickName
	) {
		return ResponseEntityFactory.ok(
			"카드 닉네임이 성공적으로 변경되었습니다.",
			signatureDetailCardService.cardNickNameModify(
				memberId,
				cardId,
				cardNickName)
		);
	}

	@GetMapping("/v1/given-cards")
	public ResponseEntity<MessageBody<GivenCardListResponse>> getGivenCardList(
		@RequestHeader(name = "Member-Id", required = true) Long memberId
	) {
		return ResponseEntityFactory.ok("내가 빌려준 카드 목록 조회 성공",
			signatureDetailCardService.getGivenCardList(memberId));
	}

	@GetMapping("/v1/given-cards/{card_id}")
	public ResponseEntity<MessageBody<GivenCardDetailListResposne>> getGivenCardDetailList(
		@RequestHeader(name = "Member-Id", required = true) Long memberId,
		@PathVariable(name = "card_id") Long cardId) {

		GivenCardDetailListResposne response = signatureDetailCardService.getGivenCardDetailList(cardId, memberId);
		return ResponseEntityFactory.ok("내가 빌려준 카드 세부 내역 조회 성공", response);
	}
}
