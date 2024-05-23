package samsung.signature.signatureservice.encryption;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;
import samsung.signature.signatureservice.signature.domain.SignatureToken;
import samsung.signature.signatureservice.signature.domain.SignatureTokenInfo;

@Component
public class HybridTokenProvider {
	private static final String RSA_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
	private static final String AES_TRANSFORMATION = "AES/ECB/PKCS5Padding";
	private final KeyFactory keyFactory;
	private final KeyGenerator keyGenerator;
	private final ObjectMapper objectMapper;

	@SneakyThrows
	public HybridTokenProvider(ObjectMapper objectMapper) {

		this.keyFactory = KeyFactory.getInstance("RSA");
		this.keyGenerator = KeyGenerator.getInstance("AES");
		this.objectMapper = objectMapper;
	}

	public SignatureTokenInfo generateSignatureToken(
		final SignatureToken signatureToken,
		final String toPrivateKey,
		final String fromPublicKey
	) throws
		IllegalBlockSizeException,
		NoSuchPaddingException,
		InvalidKeySpecException,
		BadPaddingException,
		NoSuchAlgorithmException,
		InvalidKeyException,
		JsonProcessingException {
		// 카드 토큰 요청자의 public key로 암호화
		String encodedToken = encodeByAsy(
			signatureToken.getCardToken().getBytes(StandardCharsets.UTF_8),
			getPublicKey(fromPublicKey)
		);
		signatureToken.updateCardToken(encodedToken);
		// 대칭키 생성
		keyGenerator.init(256);
		SecretKey secretKey = keyGenerator.generateKey();
		// 대칭키로 시그니처 토큰 객체 암호화
		String encryptedSignatureToken = covertByteToString(encodeBySym(signatureToken, secretKey));
		// 승인자의 private key로 대칭키 암호화

		String encryptedSecretKey = encodeByAsy(
			secretKey.getEncoded(),
			getPrivateKey(toPrivateKey)
		);
		return SignatureTokenInfo.of(encryptedSignatureToken, encryptedSecretKey);
	}

	private byte[] encodeBySym(
		final SignatureToken signatureToken,
		final SecretKey secretKey
	)
		throws
		NoSuchPaddingException,
		NoSuchAlgorithmException,
		InvalidKeyException,
		IllegalBlockSizeException,
		BadPaddingException,
		JsonProcessingException {
		Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		return cipher.doFinal(
			objectMapper.writeValueAsString(signatureToken).getBytes(StandardCharsets.UTF_8)
		);
	}

	private String encodeByAsy(
		final byte[] plainText,
		final Key key
	) throws
		InvalidKeyException,
		IllegalBlockSizeException,
		BadPaddingException,
		NoSuchPaddingException,
		NoSuchAlgorithmException {
		Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return covertByteToString(cipher.doFinal(plainText));
	}

	public SignatureToken decodeBySym(
		final String plainText,
		final Key secretKey
	) throws
		IllegalBlockSizeException,
		NoSuchPaddingException,
		BadPaddingException,
		NoSuchAlgorithmException,
		InvalidKeyException, IOException {
		Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
		byte[] encryptedPlainTextByte = covertStringToByte(plainText);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] decodedSignatureToken = cipher.doFinal(encryptedPlainTextByte);
		return objectMapper.readValue(decodedSignatureToken, SignatureToken.class);
	}

	private byte[] decodeByAsy(
		final String encryptedPlainText,
		final Key key
	) throws
		InvalidKeyException,
		IllegalBlockSizeException,
		BadPaddingException,
		NoSuchPaddingException,
		NoSuchAlgorithmException {
		Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(covertStringToByte(encryptedPlainText));
	}

	private PrivateKey getPrivateKey(final String privateKey) throws InvalidKeySpecException {
		byte[] bytes = covertStringToByte(privateKey);
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
		return keyFactory.generatePrivate(spec);
	}

	private PublicKey getPublicKey(final String publicKey) throws InvalidKeySpecException {
		byte[] bytes = covertStringToByte(publicKey);
		X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
		return keyFactory.generatePublic(spec);
	}

	private byte[] covertStringToByte(final String key) {
		return Base64.getDecoder().decode(key);
	}

	private String covertByteToString(final byte[] key) {
		return Base64.getEncoder().encodeToString(key);
	}

	public SignatureToken decodeSignatureToken(
		final SignatureTokenInfo signatureTokenInfo,
		final String fromPrivateKey,
		final String toPublicKey
	) throws
		InvalidKeySpecException,
		IllegalBlockSizeException,
		NoSuchPaddingException,
		BadPaddingException,
		NoSuchAlgorithmException,
		InvalidKeyException, IOException {
		// 승인자의 public key로 대칭키 복호화
		byte[] decodedSecretKey = decodeByAsy(
			signatureTokenInfo.getEncodedSecretKey(),
			getPublicKey(toPublicKey)
		);
		// 대칭키 생성
		SecretKey secretKey = new SecretKeySpec(decodedSecretKey, "AES");
		// 대칭키로 시그니처 토큰 객체 복호화
		SignatureToken decodeSignatureToken = decodeBySym(signatureTokenInfo.getEncodedToken(), secretKey);
		// 카드 토큰 요청자의 private key로 복호화
		String decodedCardToken = new String(
			decodeByAsy(
				decodeSignatureToken.getCardToken(),
				getPrivateKey(fromPrivateKey)
			),
			StandardCharsets.UTF_8
		);
		return decodeSignatureToken.updateCardToken(decodedCardToken);
	}
}
