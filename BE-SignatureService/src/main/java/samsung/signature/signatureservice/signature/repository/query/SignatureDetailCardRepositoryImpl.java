package samsung.signature.signatureservice.signature.repository.query;

import static samsung.signature.signatureservice.member.domain.QMember.*;
import static samsung.signature.signatureservice.signature.domain.QSignatureDetail.*;
import static samsung.signature.signatureservice.signature.domain.QSignatureDetailCard.*;

import java.util.List;
import java.util.Optional;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import samsung.signature.signatureservice.signature.domain.SignatureDetailCard;
import samsung.signature.signatureservice.signature.dto.response.GivenCardDetailListResposne;
import samsung.signature.signatureservice.signature.dto.response.GivenCardDetailResponse;
import samsung.signature.signatureservice.signature.repository.condition.SignatureDetailCondition;

@RequiredArgsConstructor
public class SignatureDetailCardRepositoryImpl implements SignatureDetailCardRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<SignatureDetailCard> findApprovedCardByCardId(
		final Long memberId,
		final Long signatureDetailCardId
	) {
		return Optional.ofNullable(
			queryFactory.select(signatureDetailCard)
				.from(signatureDetail)
				.join(signatureDetail.signatureDetailCard, signatureDetailCard)
				.where(signatureDetail.from.id.eq(memberId)
					.and(signatureDetailCard.id.eq(signatureDetailCardId))
					.and(SignatureDetailCondition.isValidateCard())
				)
				.fetchFirst()
		);
	}

	@Override
	public Optional<SignatureDetailCard> findAllApprovedCardsByCardId(
		final Long memberId,
		final Long signatureDetailCardId) {
		return Optional.ofNullable(
			queryFactory.select(signatureDetailCard)
				.from(signatureDetail)
				.join(signatureDetail.signatureDetailCard, signatureDetailCard)
				.where(signatureDetail.from.id.eq(memberId)
					.and(signatureDetailCard.id.eq(signatureDetailCardId))
					.and(SignatureDetailCondition.isApproved())
					.and(SignatureDetailCondition.isNotDeleted())
				)
				.fetchFirst()
		);
	}

	@Override
	public List<Long> findAllGivenCardByMemberId(Long memberId) {
		return queryFactory.select(signatureDetailCard.cardId)
			.from(signatureDetail)
			.join(signatureDetail.signatureDetailCard, signatureDetailCard)
			.where(signatureDetail.to.id.eq(memberId)
				.and(SignatureDetailCondition.isApproved())
				.and(SignatureDetailCondition.isNotDeleted()))
			.distinct()
			.fetch();
	}

	// -------------------*******************---------------------
	@Override
	public GivenCardDetailListResposne findAllGivenCardDetailByGivenCardIdAndMemberId(Long cardId, Long memberId) {
		List<GivenCardDetailResponse> list = queryFactory.select(Projections.constructor(GivenCardDetailResponse.class,
				signatureDetailCard.id.as("given_card_id"),
				signatureDetail.from.userName.as("card_username"),
				signatureDetailCard.isValidate.as("validate_type")
			))
			.from(signatureDetail)
			.join(signatureDetail.signatureDetailCard, signatureDetailCard)
			.join(signatureDetail.to, member)
			.where(signatureDetailCard.cardId.eq(cardId)
				.and(signatureDetail.to.id.eq(memberId))
				.and(SignatureDetailCondition.isApproved())
				.and(SignatureDetailCondition.isNotDeleted())
			)
			.fetch();

		return GivenCardDetailListResposne.builder().givenCardDetailResponseList(list).build();
	}
}
