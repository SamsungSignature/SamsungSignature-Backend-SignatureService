package samsung.signature.signatureservice.signature.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import samsung.signature.signatureservice.signature.domain.Condition;

@Repository
public interface ConditionRepository extends JpaRepository<Condition, Long> {
}
