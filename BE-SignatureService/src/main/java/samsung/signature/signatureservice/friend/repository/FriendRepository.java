package samsung.signature.signatureservice.friend.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import samsung.signature.signatureservice.friend.domain.Friend;
import samsung.signature.signatureservice.friend.repository.query.FriendRepositoryCustom;
import samsung.signature.signatureservice.member.domain.Member;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long>, FriendRepositoryCustom {
	@Query("SELECT f.to FROM Friend f WHERE f.from = :member")
	Set<Member> findAllFriendIdsSetByFromId(Member member);

	Optional<Friend> findByFrom_IdAndTo_Id(Long fromId, Long toId);
}
