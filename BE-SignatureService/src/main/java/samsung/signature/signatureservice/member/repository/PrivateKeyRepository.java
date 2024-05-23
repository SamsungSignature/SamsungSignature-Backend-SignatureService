package samsung.signature.signatureservice.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import samsung.signature.signatureservice.member.domain.PrivateKey;

@Repository
public interface PrivateKeyRepository extends JpaRepository<PrivateKey, Long> {
	@Query("SELECT pk.privateKey "
		+ "FROM PrivateKey pk "
		+ "WHERE pk.memberId = :memberId")
	Optional<String> findByMemberId(@Param("memberId") final Long memberId);
}
