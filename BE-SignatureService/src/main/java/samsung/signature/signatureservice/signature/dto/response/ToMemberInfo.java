package samsung.signature.signatureservice.signature.dto.response;

import com.querydsl.core.annotations.QueryProjection;

import samsung.signature.signatureservice.member.domain.Member;

public record ToMemberInfo(
	String name,
	Member member
) {
	@QueryProjection
	public ToMemberInfo(final String name, final Member member) {
		this.name = name;
		this.member = member;
	}
}
