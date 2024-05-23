package samsung.signature.signatureservice.global.client;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import samsung.signature.signatureservice.signature.dto.request.AddSignatureCardToWalletRequest;
import samsung.signature.signatureservice.signature.dto.response.CardInfo;
import samsung.signature.signatureservice.signature.dto.response.CardInfoToken;
import samsung.signature.signatureservice.signature.dto.response.ExistsSignatureCardResponse;

@FeignClient(name = "wallet-service")
public interface WalletServiceClient {

	@PostMapping("/wallet-service/v1/card-infos")
	Map<Long, CardInfo> getCardInfos(
		@RequestBody final List<Long> cardIds
	);

	@PostMapping("/wallet-service/v1/cards")
	void addSignatureCard(
		@RequestHeader(name = "Member-Id", required = true) long memberId,
		@RequestBody AddSignatureCardToWalletRequest request);

	@GetMapping("/wallet-service/test")
	void getTest();

	@GetMapping("/wallet-service/v1/cards/{card_id}")
	CardInfoToken getCardInfoToken(
		@RequestHeader(name = "Member-Id", required = true) Long memberId,
		@PathVariable(name = "card_id") Long cardId
	);

	@GetMapping("/wallet-service/v1/signature")
	ExistsSignatureCardResponse isExistsSignatureCard(
		@RequestHeader(name = "Member-Id", required = true) Long memberId);

	@PutMapping("/wallet-service/v1/signature-tokens")
	void saveSignatureToken(
		@RequestHeader(name = "Member-Id", required = true) Long memberId,
		@RequestBody String encodedToken
	);
	@DeleteMapping("/wallet-service/v1/signature-tokens/{member_id}")
	void deleteSignatureToken(@PathVariable("member_id") final Long memberId);
}
