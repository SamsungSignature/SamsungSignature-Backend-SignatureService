package samsung.signature.signatureservice.friend.domain;

import samsung.signature.signatureservice.global.domain.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import samsung.signature.signatureservice.member.domain.Member;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "friends_tbl")
public class Friend extends BaseTime {
// TODO: 인덱싱
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "friend_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "from_member_id")
	private Member from;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "to_member_id")
	private Member to;

	@NotBlank
	@Column(name = "friend_name")
	String friendName;

	public static Friend of(
		final Member from,
		final Member to,
		final String friendName
	) {
		return Friend.builder()
			.from(from)
			.to(to)
			.friendName(friendName)
			.build();
	}
}

