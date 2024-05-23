package samsung.signature.signatureservice.signature.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import samsung.signature.signatureservice.global.client.WalletServiceClient;
import samsung.signature.signatureservice.signature.domain.SignatureDetailCard;
import samsung.signature.signatureservice.signature.dto.request.CardModifiedInfo;
import samsung.signature.signatureservice.signature.dto.response.*;
import samsung.signature.signatureservice.signature.repository.SignatureDetailCardRepository;
import samsung.signature.signatureservice.signature.repository.SignatureDetailRepository;
import samsung.signature.signatureservice.signature.repository.SignatureTokenInfoRepository;
import samsung.signature.signatureservice.signature.util.SignatureDetailCardServiceUtil;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SignatureDetailCardService {
	private final SignatureDetailCardRepository signatureDetailCardRepository;
	private final SignatureDetailRepository signatureDetailRepository;
	private final WalletServiceClient walletServiceClient;
	private final SignatureTokenInfoRepository signatureTokenInfoRepository;
	@Transactional
	public void deleteDelegatedCard(
		final Long memberId,
		final Long signatureDetailCardId
	) {
		SignatureDetailCard delegatedCard = SignatureDetailCardServiceUtil.findApprovedDelegatedCard(
			signatureDetailCardRepository,
			memberId,
			signatureDetailCardId
		);
		delegatedCard.softDelete();
	}

	public DelegatedCards getApprovedCards(
		final Long memberId
	) {
		List<DelegatedCard> delegatedCards = signatureDetailRepository.findAllApprovedAndUsableByMemberId(memberId);
		return DelegatedCards.from(updateDelegatedCardInfo(delegatedCards));
	}

	private List<DelegatedCard> updateDelegatedCardInfo(
		final List<DelegatedCard> delegatedCards) {
		// 승인받은 카드 pk 리스트
		List<Long> cardIds = delegatedCards.stream()
			.map(DelegatedCard::getCardId)
			.toList();
		// 승인받은 카드 정보 조회
		Map<Long, CardInfo> cardInfos = walletServiceClient.getCardInfos(cardIds);

		return delegatedCards.stream()
			.filter(card -> signatureTokenInfoRepository.existsById(card.getSignatureDetailCardId()))
			.filter(card -> cardInfos.containsKey(card.getCardId()))
			.map(card -> card.update(
					cardInfos.get(card.getCardId()),
					signatureTokenInfoRepository.findById(card.getSignatureDetailCardId()).get().getTtl()
				)
			)
			.toList();
	}

	@Transactional
	public DelegatedCard cardNickNameModify(
		final Long memberId,
		final Long signatureDetailCardId,
		final CardModifiedInfo cardModifiedInfo
	) {
		SignatureDetailCard signatureDetailCard = SignatureDetailCardServiceUtil.findDelegatedCard(
			signatureDetailCardRepository,
			memberId,
			signatureDetailCardId
		);
		signatureDetailCard.modifyCardNickName(cardModifiedInfo.cardNickName());
		return DelegatedCard.from(signatureDetailCard);
	}

	@Transactional
	public GivenCardListResponse getGivenCardList(final Long memberId) {
		List<Long> givenCardIdList = signatureDetailCardRepository.findAllGivenCardByMemberId(memberId);

		Map<Long, CardInfo> cardInfos = walletServiceClient.getCardInfos(givenCardIdList);

		List<GivenCard> list =
			givenCardIdList.stream()
				.filter(cardInfos::containsKey)
				.map(cardId -> {
					CardInfo cardInfo = cardInfos.get(cardId);
					return GivenCard.builder()
						.id(cardInfo.getId())
						.cardImg(cardInfo.getCardImg())
						.name(cardInfo.getCardName())
						.build();
				})
				.toList();

		return GivenCardListResponse.builder().givenCardList(list).build();
	}

	// ---------------***************----------------
	public GivenCardDetailListResposne getGivenCardDetailList(Long cardId, Long memberId) {
		return signatureDetailCardRepository.findAllGivenCardDetailByGivenCardIdAndMemberId(cardId, memberId);
	}
}
