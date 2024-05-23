package samsung.signature.signatureservice.signature.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import samsung.signature.common.exception.SignatureException;
import samsung.signature.signatureservice.encryption.HybridTokenProvider;
import samsung.signature.signatureservice.encryption.SignatureTokenUtil;
import samsung.signature.signatureservice.friend.repository.FriendRepository;
import samsung.signature.signatureservice.friend.util.FriendServiceUtil;
import samsung.signature.signatureservice.global.client.WalletServiceClient;
import samsung.signature.signatureservice.member.domain.Member;
import samsung.signature.signatureservice.member.repository.MemberRepository;
import samsung.signature.signatureservice.member.repository.PrivateKeyRepository;
import samsung.signature.signatureservice.notification.domain.NotificationType;
import samsung.signature.signatureservice.notification.service.NotificationService;
import samsung.signature.signatureservice.signature.domain.*;
import samsung.signature.signatureservice.signature.dto.request.LongTermSignatureConstraint;
import samsung.signature.signatureservice.signature.dto.request.OneTimeSignatureConstraint;
import samsung.signature.signatureservice.signature.dto.request.SignatureConstraint;
import samsung.signature.signatureservice.signature.dto.request.SignatureDetailPermission;
import samsung.signature.signatureservice.signature.dto.response.CardInfoToken;
import samsung.signature.signatureservice.signature.dto.response.LongTermDelegatedContractResponse;
import samsung.signature.signatureservice.signature.dto.response.OneTimeDelegatedContractResponse;
import samsung.signature.signatureservice.signature.dto.response.SignatureDetailPermissionResult;
import samsung.signature.signatureservice.signature.dto.response.ToMemberInfo;
import samsung.signature.signatureservice.signature.exception.SignatureDetailErrorCode;
import samsung.signature.signatureservice.signature.repository.ConditionRepository;
import samsung.signature.signatureservice.signature.repository.SignatureDelegatedConstraintRepository;
import samsung.signature.signatureservice.signature.repository.SignatureDetailCardRepository;
import samsung.signature.signatureservice.signature.repository.SignatureDetailRepository;
import samsung.signature.signatureservice.signature.repository.SignatureTokenInfoRepository;
import samsung.signature.signatureservice.signature.util.SignatureServiceUtil;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SignatureDelegatedCardService {
	private final SignatureDetailRepository signatureDetailRepository;
	private final SignatureDetailCardRepository signatureDetailCardRepository;
	private final ConditionRepository conditionRepository;
	private final MemberRepository memberRepository;
	private final FriendRepository friendRepository;
	private final SignatureDelegatedConstraintRepository signatureDelegatedConstraintRepository;
	private final PrivateKeyRepository privateKeyRepository;
	private final WalletServiceClient walletServiceClient;
	private final HybridTokenProvider hybridTokenProvider;
	private final NotificationService notificationService;
	private final SignatureTokenInfoRepository signatureTokenInfoRepository;
	private final RedisTemplate<byte[], byte[]> redisTemplate;
	private static final String SIGNATURE_STATUS_PREFIX = "signature-status:";

	private static long calculateTTL(LocalDate limitDate) {
		ZonedDateTime zonedDateTime = limitDate.atTime(LocalTime.MIDNIGHT)
			.atZone(ZoneId.of("Asia/Seoul"));
		long limitMillis = zonedDateTime.toInstant().toEpochMilli();
		return (limitMillis - System.currentTimeMillis()) / 1000L;
	}

	@Transactional
	public OneTimeDelegatedContractResponse sendOneTimeDelegatedContract(
		final Long memberId,
		final OneTimeSignatureConstraint delegatedContractRequest
	) {
		Member loginMember = memberRepository.getReferenceById(memberId);
		// 요청한 사람과 친구인지 확인
		ToMemberInfo friend = FriendServiceUtil.getMemberFriend(
			friendRepository,
			loginMember,
			delegatedContractRequest.toId()
		);
		// 친구이면 요청시도
		SignatureDetail signatureDetail = generateDelegatedContract(loginMember, friend.member(),
			delegatedContractRequest);
		// 대리 결제 요청 알림
		notificationService.publish(signatureDetail.getTo().getId(), NotificationType.INPROGRESS_SUBMIT, signatureDetail);
		return OneTimeDelegatedContractResponse.of(friend, delegatedContractRequest);
	}

	private SignatureDetail generateDelegatedContract(
		final Member from,
		final Member to,
		final SignatureConstraint delegatedContractRequest
	) {
		// 대리 결제 조건 저장
		Condition condition = Condition.from(delegatedContractRequest);
		conditionRepository.save(condition);

		// 대리 결제 요청서 저장
		SignatureDetail signatureDetail =
			SignatureDetail.of(from, to, condition, SignatureType.INSTANT, PermissionStatus.INPROGRESS);
		signatureDetailRepository.save(signatureDetail);

		// 레디스에 대리 결제 정보 캐싱
		SignatureDelegatedConstraint signatureDelegatedConstraint =
			SignatureDelegatedConstraint.of(signatureDetail, condition);
		signatureDelegatedConstraintRepository.save(signatureDelegatedConstraint);

		// Notification SignatureDetail Return
		return signatureDetail;
	}

	@Transactional
	public LongTermDelegatedContractResponse sendLongTermDelegatedContract(
		final Long memberId,
		final LongTermSignatureConstraint longTermDelegatedContractRequest
	) {
		Member loginMember = memberRepository.getReferenceById(memberId);
		// 요청한 사람과 친구인지 확인
		List<ToMemberInfo> friends =
			friendRepository.findAllFriendByToId(
				loginMember,
				longTermDelegatedContractRequest.toIds()
			);
		// 친구이면 요청시도
		CardInfoToken cardInfoToken = generateBusinessDelegatedContract(loginMember, friends,
			longTermDelegatedContractRequest);
		return LongTermDelegatedContractResponse.of(friends, longTermDelegatedContractRequest, cardInfoToken);
	}

	private CardInfoToken generateBusinessDelegatedContract(
		final Member to,
		final List<ToMemberInfo> from,
		final SignatureConstraint signatureConstraint
	) {
		List<Condition> conditions = new LinkedList<>();
		List<SignatureDetail> signatureDetails = new ArrayList<>();
		from.forEach(friend -> {
			// 대리 결제 조건 저장
			Condition condition = Condition.from(signatureConstraint);
			conditions.add(condition);

			// 대리 결제 요청서 저장
			SignatureDetail signatureContract =
				SignatureDetail.of(friend.member(), to, condition, SignatureType.DURATION, PermissionStatus.APPROVED);
			signatureDetails.add(signatureContract);
		});
		conditionRepository.saveAll(conditions);
		signatureDetailRepository.saveAll(signatureDetails);
		// 대리 결제 요청서 돌면서 시그니처 토큰 만들기
		// 카드 토큰 가져오기
		CardInfoToken cardInfoToken = walletServiceClient.getCardInfoToken(
			to.getId(),
			signatureConstraint.getCardId()
		);
		// 승인자가 선택한 카드 정보 저장
		List<SignatureDetailCard> signatureDetailCards = new LinkedList<>();

		signatureDetails.forEach(signatureDetail ->
			signatureDetailCards.add(SignatureDetailCard.of(signatureDetail, cardInfoToken)));
		signatureDetailCardRepository.saveAll(signatureDetailCards);

		List<SignatureTokenInfo> signatureTokenInfos = new LinkedList<>();

		signatureDetails.forEach(signatureDetail -> {
			long ttl = calculateTTL(signatureConstraint.getLimitDate());
			signatureTokenInfos.add(createSignatureToken(cardInfoToken, signatureDetail, signatureConstraint, ttl));
			// 법인카드 알람
			notificationService.publish(signatureDetail.getFrom().getId(), NotificationType.ISSUED, signatureDetail);
		});

		signatureTokenInfoRepository.saveAll(signatureTokenInfos);
		return cardInfoToken;
	}

	@Transactional
	public SignatureDetailPermissionResult changeDelegatedCardPermission(
		final Long memberId,
		final Long signatureDetailId,
		final SignatureDetailPermission signatureDetailPermission
	) {
		// 대리 결제 요청서 조회(시간경과되었는지 여부 레디스에서 조회)
		SignatureDelegatedConstraint signatureDelegatedConstraint = signatureDelegatedConstraintRepository.findById(
				signatureDetailId)
			.orElseThrow(() -> {
				// 해당 요청서 거절로 상태 변경
				signatureDetailRepository.invalidSignatureDetail(signatureDetailId);
				return new SignatureException(SignatureDetailErrorCode.NOT_IN_PROGRESS_SIGNATURE_DETAIL);
			});

		// 요청 진행중인 대리 결제 요청서 조회(경과되지 않았으면 db에서 조회)
		SignatureDetail signatureDetail = SignatureServiceUtil.changeStatusSignatureDetail(
			signatureDetailRepository,
			signatureDetailId,
			signatureDetailPermission.getPermission()
		);
		// 승인일 경우
		if (PermissionStatus.APPROVED.equals(signatureDetailPermission.getPermission())) {
			// 카드 토큰 가져오기
			CardInfoToken cardInfoToken = walletServiceClient.getCardInfoToken(
				memberId,
				signatureDetailPermission.cardId()
			);

			// 승인자가 선택한 카드 정보 저장
			SignatureDetailCard signatureDetailCard = SignatureDetailCard.of(signatureDetail, cardInfoToken);
			signatureDetailCardRepository.save(signatureDetailCard);

			signatureTokenInfoRepository.save(createSignatureToken(
				cardInfoToken,
				signatureDetail,
				signatureDelegatedConstraint,
				300L // 5분
			));
		}
		// 레디스에서 진행중인 대리 결제 요청서 목록에서 삭제
		signatureDelegatedConstraintRepository.delete(signatureDelegatedConstraint);

		// 대리 결제 승인 결과 변경 알림
		notificationService.publish(signatureDetail.getFrom().getId(),
			PermissionStatus.APPROVED.equals(signatureDetailPermission.getPermission())
				? NotificationType.APPROVED
				: NotificationType.REJECTED,
			signatureDetail);

		redisTemplate.delete((SIGNATURE_STATUS_PREFIX + signatureDetailId).getBytes(StandardCharsets.UTF_8));

		return SignatureDetailPermissionResult.of(
			signatureDetail,
			signatureDelegatedConstraint
		);
	}

	private SignatureTokenInfo createSignatureToken(
		final CardInfoToken cardInfoToken,
		final SignatureDetail signatureDetail,
		final SignatureConstraint signatureConstraint,
		final long ttl
	) {
		// 시그니처 토큰만들기
		SignatureTokenInfo signatureTokenInfo = SignatureTokenUtil.generateSignatureToken(
			hybridTokenProvider,
			memberRepository,
			privateKeyRepository,
			signatureDetail,
			cardInfoToken,
			signatureConstraint
		);
		// 레디스 저장
		return signatureTokenInfo.updateTokenInfo(signatureDetail, ttl);
	}
}
