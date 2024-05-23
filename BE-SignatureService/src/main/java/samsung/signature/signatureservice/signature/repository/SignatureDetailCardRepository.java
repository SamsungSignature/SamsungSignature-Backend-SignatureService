package samsung.signature.signatureservice.signature.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import samsung.signature.signatureservice.signature.domain.SignatureDetailCard;
import samsung.signature.signatureservice.signature.repository.query.SignatureDetailCardRepositoryCustom;

@Repository
public interface SignatureDetailCardRepository
	extends JpaRepository<SignatureDetailCard, Long>, SignatureDetailCardRepositoryCustom {
}
