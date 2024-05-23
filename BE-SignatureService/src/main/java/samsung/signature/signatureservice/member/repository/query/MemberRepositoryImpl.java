package samsung.signature.signatureservice.member.repository.query;

import static samsung.signature.signatureservice.member.domain.QMember.member;

import java.util.List;
import java.util.Set;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import samsung.signature.signatureservice.member.domain.Member;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<Member> findAllFriendsByPhoneNumbers(Set<String> phoneNumbers) {
		return queryFactory.selectFrom(member)
			.where(member.phoneNumber.in(phoneNumbers))
			.fetch();
	}
}
