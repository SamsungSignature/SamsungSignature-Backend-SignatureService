package samsung.signature.signatureservice.signature.repository.query;

import static samsung.signature.signatureservice.member.domain.QMember.*;
import static samsung.signature.signatureservice.signature.domain.QCondition.*;
import static samsung.signature.signatureservice.signature.domain.QSignatureDetail.*;
import static samsung.signature.signatureservice.signature.domain.QSignatureDetailCard.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import samsung.signature.signatureservice.member.domain.Member;
import samsung.signature.signatureservice.signature.domain.PermissionStatus;
import samsung.signature.signatureservice.signature.dto.response.DelegatedCard;
import samsung.signature.signatureservice.signature.dto.response.QDelegatedCard;
import samsung.signature.signatureservice.signature.dto.response.QSignatureHistoryResponse;
import samsung.signature.signatureservice.signature.dto.response.SignatureHistoryListResponse;
import samsung.signature.signatureservice.signature.dto.response.SignatureHistoryResponse;
import samsung.signature.signatureservice.signature.repository.condition.SignatureDetailCondition;

@RequiredArgsConstructor
public class SignatureDetailRepositoryCustomImpl implements SignatureDetailRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public SignatureHistoryListResponse findAllAppliedSignatureDetailByMemberId(Long memberId) {
		List<SignatureHistoryResponse> list = queryFactory.select(new QSignatureHistoryResponse(
				signatureDetail.id,
				member.userName,
				signatureDetail.permissionStatus,
				localDateTimeToLocalDateString(signatureDetail.createdAt),
				signatureDetail.condition.limitAmount
			))
			.from(signatureDetail)
			.join(signatureDetail.condition, condition)
			.join(signatureDetail.to, member)
			.where(signatureDetail.from.id.eq(memberId))
			.orderBy(signatureDetail.createdAt.desc())
			.fetch();

		return SignatureHistoryListResponse.builder().historyList(list).build();
	}

	@Override
	public SignatureHistoryListResponse findAllApprovedSignatureDetailByMemberId(Long memberId) {
		List<SignatureHistoryResponse> list = queryFactory.select(new QSignatureHistoryResponse(
				signatureDetail.id,
				member.userName,
				signatureDetail.permissionStatus,
				localDateTimeToLocalDateString(signatureDetail.createdAt),
				signatureDetail.condition.limitAmount
			))
			.from(signatureDetail)
			.join(signatureDetail.condition, condition)
			.join(signatureDetail.from, member)
			.where(signatureDetail.to.id.eq(memberId))
			.orderBy(signatureDetail.updatedAt.desc())
			.fetch();

		return SignatureHistoryListResponse.builder().historyList(list).build();
	}

	@Override
	public List<DelegatedCard> findAllApprovedAndUsableByMemberId(final long memberId) {
		return queryFactory.select(new QDelegatedCard(
				signatureDetail.id,
				signatureDetailCard.id,
				signatureDetailCard.cardId,
				signatureDetailCard.nickname,
				member.userName
			))
			.from(signatureDetail)
			.join(signatureDetail.condition, condition)
			.join(signatureDetail.signatureDetailCard, signatureDetailCard)
			.join(signatureDetail.to, member)
			.where(signatureDetail.from.id.eq(memberId)
				.and(SignatureDetailCondition.isValidateCard())
			)
			.fetch();
	}

	@Override
	public boolean invalidSignatureDetail(final Long signatureDetailId) {
		long affectedRows = queryFactory.update(signatureDetail)
			.set(signatureDetail.permissionStatus, PermissionStatus.REJECTED)
			.where(signatureDetail.id.eq(signatureDetailId))
			.execute();
		return affectedRows > 0;

	}

	private static StringTemplate localDateTimeToLocalDateString(Path<LocalDateTime> column) {
		return Expressions.stringTemplate("date_format({0},'%Y-%m-%d')", column);
	}

	private static StringTemplate localDateToString(Path<LocalDate> column) {
		return Expressions.stringTemplate("date_format({0},'%Y-%m-%d')", column);
	}

	@Override
	public Optional<Member> findMemberBySignatureDetailCardId(Long signatureDetailCardId) {
		return Optional.ofNullable(
			queryFactory.select(member)
				.from(signatureDetail)
				.join(signatureDetail.signatureDetailCard, signatureDetailCard)
				.join(signatureDetail.from, member)
				.where(signatureDetailCard.id.eq(signatureDetailCardId))
				.fetchFirst()
		);
	}
}
