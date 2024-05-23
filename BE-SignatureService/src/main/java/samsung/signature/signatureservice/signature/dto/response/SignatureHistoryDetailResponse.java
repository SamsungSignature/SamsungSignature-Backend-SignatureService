package samsung.signature.signatureservice.signature.dto.response;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryProjection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import samsung.signature.signatureservice.friend.domain.Friend;
import samsung.signature.signatureservice.signature.domain.PermissionStatus;
import samsung.signature.signatureservice.signature.domain.ProgressType;
import samsung.signature.signatureservice.signature.domain.SignatureDetail;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignatureHistoryDetailResponse {
	@JsonProperty("signature_detail_id") private Long signatureDetailId;
	@JsonProperty("limit_amount") private int limitAmount;
	@JsonProperty("signature_date") private String signatureDate;
	@JsonProperty("signature_name") private String signatureName;
	@JsonProperty("limit_date") private String limitDate;
	@JsonProperty("market_name") private String marketName;
	@JsonProperty("item") private String item;
	@JsonProperty("item_image") private String itemImage;
	@JsonProperty("permission_status") private PermissionStatus permissionStatus;
	@JsonProperty("progress_status") private ProgressType progressType;

	@QueryProjection
	public SignatureHistoryDetailResponse(
		Long signatureDetailId, int limitAmount, String signatureDate,
		String signatureName,
		String limitDate, String marketName, String item, String itemImage,
		PermissionStatus permissionStatus
		) {

			this.signatureDetailId = signatureDetailId;
			this.limitAmount = limitAmount;
			this.signatureDate = signatureDate;
			this.signatureName = signatureName;
			this.limitDate = limitDate;
			this.marketName = marketName;
			this.item = item;
			this.itemImage = itemImage;
			this.permissionStatus = permissionStatus;
		}
		public static SignatureHistoryDetailResponse of(
		final SignatureDetail signatureDetail,
		final Friend friend,
		final ProgressType progressType
	) {
			return SignatureHistoryDetailResponse.builder()
				.signatureDetailId(signatureDetail.getId())
				.limitAmount(signatureDetail.getCondition().getLimitAmount())
				.signatureDate(signatureDetail.getCreatedAt().toLocalDate().toString())
				.signatureName(friend.getFriendName())
				.limitDate(Optional.ofNullable(signatureDetail.getCondition().getLimitDate())
					.map(Object::toString)
					.orElse(null))
				.marketName(signatureDetail.getCondition().getMarketName())
				.item(signatureDetail.getCondition().getItem())
				.itemImage(signatureDetail.getCondition().getItemImage())
				.permissionStatus(signatureDetail.getPermissionStatus())
				.progressType(progressType)
				.build();
		}

}
