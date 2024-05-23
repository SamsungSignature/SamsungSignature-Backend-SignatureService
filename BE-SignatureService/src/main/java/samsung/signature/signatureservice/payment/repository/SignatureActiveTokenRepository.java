package samsung.signature.signatureservice.payment.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import samsung.signature.signatureservice.payment.domain.SignatureActiveToken;

@Repository
public interface SignatureActiveTokenRepository extends CrudRepository<SignatureActiveToken, Long> {
	Optional<SignatureActiveToken> findByToken(final String token);
}
