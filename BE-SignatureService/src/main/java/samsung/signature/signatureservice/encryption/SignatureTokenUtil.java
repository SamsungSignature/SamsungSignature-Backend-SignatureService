package samsung.signature.signatureservice.encryption;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import lombok.NoArgsConstructor;
import samsung.signature.common.exception.SignatureException;
import samsung.signature.signatureservice.member.domain.Member;
import samsung.signature.signatureservice.member.exception.MemberErrorCode;
import samsung.signature.signatureservice.member.repository.MemberRepository;
import samsung.signature.signatureservice.member.repository.PrivateKeyRepository;
import samsung.signature.signatureservice.signature.domain.SignatureDetail;
import samsung.signature.signatureservice.signature.domain.SignatureToken;
import samsung.signature.signatureservice.signature.domain.SignatureTokenInfo;
import samsung.signature.signatureservice.signature.dto.request.SignatureConstraint;
import samsung.signature.signatureservice.signature.dto.response.CardInfoToken;
import samsung.signature.signatureservice.signature.exception.SignatureDetailErrorCode;
import samsung.signature.signatureservice.signature.repository.SignatureDetailRepository;
import samsung.signature.signatureservice.signature.util.SignatureServiceUtil;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class SignatureTokenUtil {
	public static SignatureTokenInfo generateSignatureToken(
		final HybridTokenProvider hybridTokenProvider,
		final MemberRepository memberRepository,
		final PrivateKeyRepository privateKeyRepository,
		final SignatureDetail signatureDetail,
		final CardInfoToken cardInfoToken,
		final SignatureConstraint signatureConstraint
	) {
		try {
			SignatureToken signatureToken = SignatureToken.of(cardInfoToken, signatureConstraint);
			String toPrivateKey = getPrivateKey(privateKeyRepository, signatureDetail.getTo());
			String fromPublicKey = getPublicKey(memberRepository, signatureDetail.getFrom());
			return hybridTokenProvider.generateSignatureToken(
				signatureToken,
				toPrivateKey,
				fromPublicKey
			);
		} catch (Exception e) {
			throw new SignatureException(SignatureDetailErrorCode.CAN_NOT_ENCODE_SIGNATURE_TOKEN);
		}
	}

	private static String getPublicKey(
		final MemberRepository memberRepository,
		final Member from
	) {
		return memberRepository.findPublicKeyById(from)
			.orElseThrow(
				() -> new SignatureException(MemberErrorCode.NOT_FOUND_MEMBER)
			);
	}

	private static String getPrivateKey(
		final PrivateKeyRepository privateKeyRepository,
		final Member member
	) {
		return privateKeyRepository.findByMemberId(member.getId())
			.orElseThrow(
				() -> new SignatureException(MemberErrorCode.NOT_FOUND_MEMBER)
			);
	}

	public static SignatureToken decodeSignatureToken(
		final HybridTokenProvider hybridTokenProvider,
		final MemberRepository memberRepository,
		final PrivateKeyRepository privateKeyRepository,
		final SignatureDetailRepository signatureDetailRepository,
		final SignatureTokenInfo signatureTokenInfo
	) throws
		IllegalBlockSizeException,
		NoSuchPaddingException,
		InvalidKeySpecException,
		BadPaddingException,
		NoSuchAlgorithmException,
		IOException,
		InvalidKeyException {
		// 받은 사람, 보낸 사람 찾기
		SignatureDetail signatureDetail = SignatureServiceUtil.getSignatureDetail(
			signatureDetailRepository,
			signatureTokenInfo.getSignatureDetailId()
		);
		String fromPrivateKey = getPrivateKey(privateKeyRepository, signatureDetail.getFrom());
		String toPublicKey = getPublicKey(memberRepository, signatureDetail.getTo());
		return hybridTokenProvider.decodeSignatureToken(
			signatureTokenInfo,
			fromPrivateKey,
			toPublicKey
		);
	}
}
