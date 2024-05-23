package samsung.signature.signatureservice.signature.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import samsung.signature.signatureservice.signature.domain.SignatureDetail;
import samsung.signature.signatureservice.signature.repository.query.SignatureDetailRepositoryCustom;

@Repository
public interface SignatureDetailRepository extends JpaRepository<SignatureDetail, Long>,
	SignatureDetailRepositoryCustom {
	Optional<SignatureDetail> findBySignatureDetailCardId(Long signatureDetailCardId);
}
