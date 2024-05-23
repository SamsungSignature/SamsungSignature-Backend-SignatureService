package samsung.signature.signatureservice.signature.repository.condition;

import static samsung.signature.signatureservice.signature.domain.QSignatureDetail.signatureDetail;
import static samsung.signature.signatureservice.signature.domain.QSignatureDetailCard.signatureDetailCard;

import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import samsung.signature.signatureservice.signature.domain.PermissionStatus;
import samsung.signature.signatureservice.signature.domain.ValidateType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SignatureDetailCondition {
	public static BooleanExpression isNotDeleted() {
		return signatureDetailCard.isDeleted.isFalse();
	}

	public static BooleanExpression isApproved() {
		return signatureDetail.permissionStatus.eq(PermissionStatus.APPROVED);
	}

	public static BooleanExpression isOnCard(){
		return signatureDetailCard.isValidate.eq(ValidateType.ON);
	}

	public static BooleanExpression isValidateCard() {
		return isNotDeleted()
			.and(isOnCard())
			.and(isApproved());
	}
}
