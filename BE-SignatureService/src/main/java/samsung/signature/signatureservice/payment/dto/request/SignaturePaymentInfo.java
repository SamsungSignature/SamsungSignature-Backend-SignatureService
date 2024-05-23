package samsung.signature.signatureservice.payment.dto.request;

public record SignaturePaymentInfo(
	Integer amount,
	String cardToken
) {
}
