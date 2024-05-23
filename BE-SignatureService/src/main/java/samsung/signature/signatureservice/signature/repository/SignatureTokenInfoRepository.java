package samsung.signature.signatureservice.signature.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import samsung.signature.signatureservice.signature.domain.SignatureTokenInfo;

@Repository
public interface SignatureTokenInfoRepository extends CrudRepository<SignatureTokenInfo, Long>{
}
