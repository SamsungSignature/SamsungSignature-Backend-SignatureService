package samsung.signature.signatureservice.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import samsung.signature.signatureservice.member.domain.Member;
import samsung.signature.signatureservice.member.repository.query.MemberRepositoryCustom;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
	@Query("SELECT m.publicKey "
		+ "FROM Member m "
		+ "WHERE m = :member")
	Optional<String> findPublicKeyById(@Param("member") final Member member);
}
