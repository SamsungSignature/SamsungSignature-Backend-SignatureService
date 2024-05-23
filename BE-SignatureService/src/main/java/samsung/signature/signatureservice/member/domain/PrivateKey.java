package samsung.signature.signatureservice.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "private_keys_tbl")
@Entity
public class PrivateKey {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "private_key_id")
	private Long id;

	@Column(name = "member_id")
	private Long memberId;

	@Column(name = "private_key", length = 1800)
	private String privateKey;

	public static PrivateKey of(
		final Long memberId,
		final String privateKey
	) {
		return PrivateKey.builder()
			.memberId(memberId)
			.privateKey(privateKey)
			.build();
	}
}
