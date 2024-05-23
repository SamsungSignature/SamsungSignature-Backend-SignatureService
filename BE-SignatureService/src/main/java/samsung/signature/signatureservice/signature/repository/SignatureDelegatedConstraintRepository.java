package samsung.signature.signatureservice.signature.repository;

import org.springframework.data.repository.CrudRepository;

import samsung.signature.signatureservice.signature.domain.SignatureDelegatedConstraint;

public interface SignatureDelegatedConstraintRepository extends CrudRepository<SignatureDelegatedConstraint, Long> {
}
