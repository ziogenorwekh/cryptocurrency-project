package shop.shportfolio.coupon.application.test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfoilo.coupon.domain.entity.CouponUsage;
import shop.shportfoilo.coupon.domain.entity.Payment;
import shop.shportfoilo.coupon.domain.exception.CouponDomainException;
import shop.shportfoilo.coupon.domain.valueobject.*;
import shop.shportfolio.common.domain.dto.payment.PaymentPayRequest;
import shop.shportfolio.common.domain.dto.payment.PaymentResponse;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.coupon.application.command.create.CouponCreateCommand;
import shop.shportfolio.coupon.application.command.create.CouponCreatedResponse;
import shop.shportfolio.coupon.application.command.track.*;
import shop.shportfolio.coupon.application.command.update.CouponCancelUpdateCommand;
import shop.shportfolio.coupon.application.command.update.CouponCancelUpdateResponse;
import shop.shportfolio.coupon.application.command.update.CouponUseUpdateCommand;
import shop.shportfolio.coupon.application.command.update.CouponUsedResponse;
import shop.shportfolio.coupon.application.exception.CouponNotFoundException;
import shop.shportfolio.coupon.application.exception.PaymentException;
import shop.shportfolio.coupon.application.mapper.CouponDataMapper;
import shop.shportfolio.coupon.application.policy.CouponDiscountPolicy;
import shop.shportfolio.coupon.application.policy.CouponHoldingPeriodPolicy;
import shop.shportfolio.coupon.application.policy.CouponUsageDatePolicy;
import shop.shportfolio.coupon.application.ports.input.CouponApplicationService;
import shop.shportfolio.coupon.application.ports.output.kafka.CouponUsedPublisher;
import shop.shportfolio.coupon.application.ports.output.payment.PaymentTossAPIPort;
import shop.shportfolio.coupon.application.ports.output.repository.CouponPaymentRepositoryPort;
import shop.shportfolio.coupon.application.ports.output.repository.CouponRepositoryPort;
import shop.shportfolio.coupon.application.test.mockbean.CouponMockBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest(classes = {CouponMockBean.class})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
public class CouponApplicationTest {

    @Autowired
    private CouponApplicationService couponApplicationService;

    @Autowired
    private CouponRepositoryPort couponRepositoryPort;

    @Autowired
    private CouponDiscountPolicy couponDiscountPolicy;


    @Autowired
    private PaymentTossAPIPort paymentTossAPIPort;
    @Autowired
    private CouponDataMapper couponDataMapper;

    @Autowired
    private CouponPaymentRepositoryPort paymentRepositoryPort;

    private PaymentPayRequest paymentPayRequest;

    @Autowired
    private CouponHoldingPeriodPolicy couponHoldingPeriodPolicy;

    @Autowired
    private CouponUsedPublisher couponUsedPublisher;

    @Autowired
    private CouponUsageDatePolicy couponUsageDatePolicy;

    private final UUID userId = UUID.randomUUID();
    List<RoleType> roleTypeWithUserAndSilver;
    List<Coupon> couponList;
    private final long amount = 10000;
    private final String paymentKey = "abc123";
    private final String orderId = "anonymous-order";

    @BeforeEach
    public void setUp() {
        couponList = new ArrayList<>();
        roleTypeWithUserAndSilver = new ArrayList<>();

        roleTypeWithUserAndSilver.add(RoleType.USER);
        roleTypeWithUserAndSilver.add(RoleType.SILVER);
        couponList.add(Coupon.createCoupon(new UserId(userId),
                couponDiscountPolicy.calculatorDiscount(roleTypeWithUserAndSilver),
                couponHoldingPeriodPolicy.calculateExpiryDate(),
                CouponCode.generate()));
        couponList.add(Coupon.createCoupon(
                new UserId(userId), couponDiscountPolicy.calculatorDiscount(roleTypeWithUserAndSilver),
                couponHoldingPeriodPolicy.calculateExpiryDate(),
                CouponCode.generate()));

        paymentPayRequest = new PaymentPayRequest(amount, orderId, paymentKey);
    }

    @Test
    @DisplayName("쿠폰 생성 테스트")
    public void createCouponTest() {
        // given
        ValidUntil validUntil = couponHoldingPeriodPolicy.calculateExpiryDate();
        FeeDiscount feeDiscount = couponDiscountPolicy.calculatorDiscount(roleTypeWithUserAndSilver);
        CouponCode couponCode = CouponCode.generate();

        CouponCreateCommand createCommand = new CouponCreateCommand(userId, roleTypeWithUserAndSilver,
                amount, orderId, paymentKey);

        Coupon mockCoupon = Coupon.createCoupon(new UserId(userId),
                feeDiscount,
                validUntil,
                couponCode);

        PaymentResponse paymentResponse = new PaymentResponse(
                paymentKey, orderId, 5000L, PaymentMethod.CARD, PaymentStatus.DONE,
                LocalDateTime.now(ZoneOffset.UTC), LocalDateTime.now(ZoneOffset.UTC), "card", "test");

        Mockito.when(paymentTossAPIPort.pay(Mockito.any())).thenReturn(paymentResponse);

        Mockito.when(couponRepositoryPort.save(Mockito.any()))
                .thenReturn(mockCoupon);

        // when
        CouponCreatedResponse couponCreatedResponse = couponApplicationService.createCoupon(createCommand);

        // then
        Mockito.verify(paymentTossAPIPort, Mockito.times(1)).pay(Mockito.any());
        Assertions.assertNotNull(couponCreatedResponse);
        Assertions.assertEquals(userId, couponCreatedResponse.getOwner());
        Assertions.assertEquals(couponCode.getValue(), couponCreatedResponse.getCouponCode());
        Assertions.assertEquals(feeDiscount.getValue(), couponCreatedResponse.getFeeDiscount());
        Assertions.assertEquals(validUntil.getValue(), couponCreatedResponse.getValidUntil());
    }


    @Test
    @DisplayName("쿠폰 리스트 조회 테스트")
    public void trackCouponListTest() {
        // given
        CouponListTrackQuery couponListTrackQuery = new CouponListTrackQuery(userId);
        Mockito.when(couponRepositoryPort.findByUserId(userId)).thenReturn(couponList);
        // when
        List<CouponTrackQueryResponse> couponTrackQueryRespons = couponApplicationService
                .trackCouponList(couponListTrackQuery);
        // then
        Assertions.assertNotNull(couponTrackQueryRespons);
        Assertions.assertEquals(2, couponTrackQueryRespons.size());
        Assertions.assertEquals(userId, couponTrackQueryRespons.get(0).getOwner());
        Assertions.assertEquals(userId, couponTrackQueryRespons.get(1).getOwner());
        Assertions.assertNotEquals(couponTrackQueryRespons.get(0).getCouponCode(),
                couponTrackQueryRespons.get(1).getCouponCode());
    }

    @Test
    @DisplayName("쿠폰 단건 조회 테스트")
    public void trackCouponTest() {
        // given
        Coupon coupon = Coupon.createCoupon(new UserId(userId),
                couponDiscountPolicy.calculatorDiscount(roleTypeWithUserAndSilver),
                couponHoldingPeriodPolicy.calculateExpiryDate(),
                CouponCode.generate());
        CouponTrackQuery couponTrackQuery = new CouponTrackQuery(userId, coupon.getId().getValue());
        Mockito.when(couponRepositoryPort.findByUserIdAndCouponId(userId, coupon.getId().getValue()))
                .thenReturn(Optional.of(coupon));
        // when
        CouponTrackQueryResponse couponTrackQueryResponse = couponApplicationService.trackCoupon(couponTrackQuery);
        // then
        Assertions.assertNotNull(couponTrackQueryResponse);
        Assertions.assertEquals(userId, couponTrackQueryResponse.getOwner());
        Assertions.assertEquals(coupon.getId().getValue(), couponTrackQueryResponse.getCouponId());
        Assertions.assertEquals(coupon.getCouponCode().getValue(), couponTrackQueryResponse.getCouponCode());
        Assertions.assertEquals(coupon.getFeeDiscount().getValue(), couponTrackQueryResponse.getFeeDiscount());
    }

    @Test
    @DisplayName("쿠퐁 사용하는 테스트")
    public void useCouponTest() {
        // given
        CouponCode generate = CouponCode.generate();
        Coupon coupon = Coupon.createCoupon(new UserId(userId),
                couponDiscountPolicy.calculatorDiscount(roleTypeWithUserAndSilver),
                couponHoldingPeriodPolicy.calculateExpiryDate(),
                generate);

        Mockito.when(couponRepositoryPort.findByUserIdAndCouponId(userId, coupon.getId().getValue()))
                .thenReturn(Optional.of(coupon));
        CouponUseUpdateCommand couponUseUpdateCommand = new CouponUseUpdateCommand(userId, coupon.getId().getValue(),
                generate.getValue());
        Coupon savedCoupon = new Coupon(coupon.getId(), coupon.getOwner(), coupon.getFeeDiscount(), coupon.getValidUntil()
                , coupon.getIssuedAt(), coupon.getCouponCode(), CouponStatus.USED);
        Mockito.when(couponRepositoryPort.save(coupon)).thenReturn(savedCoupon);
        // when
        CouponUsedResponse couponUsedResponse = couponApplicationService.useCoupon(couponUseUpdateCommand);
        // then
        Mockito.verify(couponUsedPublisher, Mockito.times(1)).publish(Mockito.any());
        Mockito.verify(couponRepositoryPort, Mockito.times(1)).saveCouponUsage(Mockito.any());
        Assertions.assertNotNull(couponUsedResponse);
        Assertions.assertEquals(userId, couponUsedResponse.getOwner());
        Assertions.assertEquals(coupon.getId().getValue(), couponUsedResponse.getCouponId());
        Assertions.assertEquals(coupon.getCouponCode().getValue(), couponUsedResponse.getCouponCode());
        Assertions.assertEquals(coupon.getFeeDiscount().getValue(), couponUsedResponse.getFeeDiscount());
        Assertions.assertEquals(CouponStatus.USED, couponUsedResponse.getStatus());
    }

    @Test
    @DisplayName("쿠폰이 만료되었는데 사용할려면 에러를 뱉어야 하는 테스트")
    public void expiredCouponUsingTest() {
        // given
        CouponCode generate = CouponCode.generate();
        Coupon coupon = Coupon.createCoupon(new UserId(userId),
                couponDiscountPolicy.calculatorDiscount(roleTypeWithUserAndSilver),
                couponHoldingPeriodPolicy.calculateExpiryDate(),
                generate);
        Coupon usedCoupon = new Coupon(coupon.getId(), coupon.getOwner(), coupon.getFeeDiscount(),
                new ValidUntil(coupon.getValidUntil().getValue().minusDays(3))
                , coupon.getIssuedAt(), coupon.getCouponCode(), CouponStatus.EXPIRED);
        Mockito.when(couponRepositoryPort.findByUserIdAndCouponId(userId, coupon.getId().getValue())).thenReturn(
                Optional.of(usedCoupon));
        CouponUseUpdateCommand couponUseUpdateCommand = new CouponUseUpdateCommand(userId, coupon.getId().getValue(),
                generate.getValue());
        // when
        CouponDomainException couponDomainException = Assertions.assertThrows(CouponDomainException.class,
                () -> couponApplicationService.useCoupon(couponUseUpdateCommand));
        // then
        Assertions.assertNotNull(couponDomainException);
        Assertions.assertEquals("Coupon is not active and cannot be used.", couponDomainException.getMessage());
    }

    @Test
    @DisplayName("쿠폰을 취소하는 테스트")
    public void cancelCouponTest() {
        // given
        PaymentResponse paymentResponse = new PaymentResponse("newPaymentKey", null,
                10000, PaymentMethod.CARD,
                PaymentStatus.CANCELED, null, null, null, null);
        CouponCode generate = CouponCode.generate();
        Coupon coupon = Coupon.createCoupon(new UserId(userId),
                couponDiscountPolicy.calculatorDiscount(roleTypeWithUserAndSilver),
                couponHoldingPeriodPolicy.calculateExpiryDate(),
                generate);
        CouponCancelUpdateCommand command = new CouponCancelUpdateCommand(userId,
                coupon.getId().getValue(), "안쓸거같아서?");
        Payment payment = Payment.createPayment(new UserId(userId), coupon.getId(), new PaymentKey("newPaymentKey"),
                new OrderPrice(BigDecimal.valueOf(10000))
                , PaymentMethod.CARD, PaymentStatus.DONE, new Description("카드결제"), "");
        Mockito.when(paymentRepositoryPort.findPaymentByUserIdAndCouponId(userId, coupon.getId().getValue())
        ).thenReturn(Optional.of(payment));
        Mockito.when(couponRepositoryPort.findByUserIdAndCouponId(userId, coupon.getId().getValue()))
                .thenReturn(Optional.of(coupon));
        Payment refundPayment = Payment.createPayment(new UserId(userId), coupon.getId(), new PaymentKey("newPaymentKey"),
                new OrderPrice(BigDecimal.valueOf(10000))
                , PaymentMethod.CARD, PaymentStatus.DONE, new Description("카드결제"), "");
        refundPayment.cancel("안쓸거같아서?");
        Mockito.when(paymentRepositoryPort.save(Mockito.any())).thenReturn(refundPayment);
        Coupon cancelledCoupon = Coupon.createCoupon(new UserId(userId),
                couponDiscountPolicy.calculatorDiscount(roleTypeWithUserAndSilver),
                couponHoldingPeriodPolicy.calculateExpiryDate(),
                generate);
        cancelledCoupon.cancel();
        Mockito.when(couponRepositoryPort.save(Mockito.any())).thenReturn(cancelledCoupon);
        Mockito.when(paymentTossAPIPort.refund(Mockito.any())).thenReturn(paymentResponse);

        // when
        CouponCancelUpdateResponse couponCancelUpdateResponse = couponApplicationService.cancelCoupon(command);
        // then
        Assertions.assertNotNull(couponCancelUpdateResponse);
        Assertions.assertNotNull(couponCancelUpdateResponse.getCanceledAt());
        Assertions.assertNotNull(couponCancelUpdateResponse.getCouponStatus());
        Assertions.assertEquals(CouponStatus.CANCELED, couponCancelUpdateResponse.getCouponStatus());
        Assertions.assertEquals("안쓸거같아서?", couponCancelUpdateResponse.getCancelReason());
    }

    @Test
    @DisplayName("결제 실패 시 쿠폰 생성 실패")
    public void couponCreatePayFailedTest() {
        // given
        ValidUntil validUntil = couponHoldingPeriodPolicy.calculateExpiryDate();
        FeeDiscount feeDiscount = couponDiscountPolicy.calculatorDiscount(roleTypeWithUserAndSilver);
        CouponCode couponCode = CouponCode.generate();
        CouponCreateCommand createCommand = new CouponCreateCommand(userId, roleTypeWithUserAndSilver,
                amount, orderId, paymentKey);
        Coupon mockCoupon = Coupon.createCoupon(new UserId(userId),
                feeDiscount,
                validUntil,
                couponCode);
        PaymentResponse paymentResponse = new PaymentResponse(
                paymentKey, orderId, 5000L, PaymentMethod.CARD, PaymentStatus.ABORTED,
                LocalDateTime.now(ZoneOffset.UTC), LocalDateTime.now(ZoneOffset.UTC), "card", "test");
        Mockito.when(paymentTossAPIPort.pay(Mockito.any())).thenReturn(paymentResponse);
        Mockito.when(couponRepositoryPort.save(Mockito.any()))
                .thenReturn(mockCoupon);

        // when
        PaymentException paymentException = Assertions.assertThrows(PaymentException.class, () ->
                couponApplicationService.createCoupon(createCommand));
        // then
        Assertions.assertNotNull(paymentException);
        Assertions.assertEquals("Payment failed", paymentException.getMessage());
    }

    @Test
    @DisplayName("다른 유저 쿠폰 접근 시 예외 처리")
    public void couponAccessOtherUserTest() {
        // given
        CouponCode generate = CouponCode.generate();
        Coupon coupon = Coupon.createCoupon(new UserId(userId),
                couponDiscountPolicy.calculatorDiscount(roleTypeWithUserAndSilver),
                couponHoldingPeriodPolicy.calculateExpiryDate(),
                generate);
        Mockito.when(couponRepositoryPort.findByUserIdAndCouponId(userId, coupon.getId().getValue()))
                .thenReturn(Optional.of(coupon));
        CouponUseUpdateCommand couponUseUpdateCommand = new CouponUseUpdateCommand(UUID.randomUUID(),
                coupon.getId().getValue(),
                generate.getValue());
        Coupon savedCoupon = new Coupon(coupon.getId(), coupon.getOwner(), coupon.getFeeDiscount(), coupon.getValidUntil()
                , coupon.getIssuedAt(), coupon.getCouponCode(), CouponStatus.USED);
        Mockito.when(couponRepositoryPort.save(coupon)).thenReturn(savedCoupon);
        // when
        CouponNotFoundException couponNotFoundException = Assertions.assertThrows(CouponNotFoundException.class, () ->
                couponApplicationService.useCoupon(couponUseUpdateCommand));
        // then
        Assertions.assertNotNull(couponNotFoundException);
        Assertions.assertEquals(String.format("coupon id %s not found",
                couponUseUpdateCommand.getCouponId()), couponNotFoundException.getMessage());
    }

    @Test
    @DisplayName("쿠폰 중복으로 사용할려면 예외되는 테스트")
    public void useCouponButAlreadyUsedTest() {
        // given
        CouponCode generate = CouponCode.generate();
        Coupon coupon = Coupon.createCoupon(new UserId(userId),
                couponDiscountPolicy.calculatorDiscount(roleTypeWithUserAndSilver),
                couponHoldingPeriodPolicy.calculateExpiryDate(),
                generate);
        coupon.useCoupon(generate.getValue());
        Mockito.when(couponRepositoryPort.save(Mockito.any())).thenReturn(coupon);
        Mockito.when(couponRepositoryPort.findByUserIdAndCouponId(userId, coupon.getId().getValue()))
                .thenReturn(Optional.of(coupon));
        CouponUseUpdateCommand couponUseUpdateCommand = new CouponUseUpdateCommand(userId, coupon.getId().getValue(),
                generate.getValue());
        // when
        CouponDomainException couponDomainException = Assertions.assertThrows(CouponDomainException.class, () ->
                couponApplicationService.useCoupon(couponUseUpdateCommand));
        // then
        Assertions.assertNotNull(couponDomainException);
        Assertions.assertEquals("Coupon is already used.", couponDomainException.getMessage());
    }

    @Test
    @DisplayName("쿠폰 코드를 잘못 입력한 경우 에러나는 테스트")
    public void wrongCodeTest() {
        // given
        CouponCode generate = CouponCode.generate();
        Coupon coupon = Coupon.createCoupon(new UserId(userId),
                couponDiscountPolicy.calculatorDiscount(roleTypeWithUserAndSilver),
                couponHoldingPeriodPolicy.calculateExpiryDate(),
                generate);
        Mockito.when(couponRepositoryPort.findByUserIdAndCouponId(userId, coupon.getId().getValue()))
                .thenReturn(Optional.of(coupon));
        CouponUseUpdateCommand couponUseUpdateCommand = new CouponUseUpdateCommand(userId,
                coupon.getId().getValue(),
                "wrongCode");
        // when
        CouponDomainException couponDomainException = Assertions.assertThrows(CouponDomainException.class, () ->
                couponApplicationService.useCoupon(couponUseUpdateCommand));
        // then
        Assertions.assertNotNull(couponDomainException);
        Assertions.assertEquals("Coupon code is invalid", couponDomainException.getMessage());
    }

    @Test
    @DisplayName("쿠폰Usage를 조회하는 테스트")
    public void retrieveCouponUsageTest() {
        // given
        CouponCode generate = CouponCode.generate();
        Coupon coupon = Coupon.createCoupon(new UserId(userId),
                couponDiscountPolicy.calculatorDiscount(roleTypeWithUserAndSilver),
                couponHoldingPeriodPolicy.calculateExpiryDate(),
                generate);
        coupon.useCoupon(generate.getValue());
        CouponUsage couponUsage = coupon.createCouponUsage();
        Mockito.when(couponRepositoryPort.findCouponUsageByUserIdAndCouponId(userId, coupon.getId().getValue()))
                .thenReturn(Optional.of(couponUsage));
        CouponUsageTrackQuery couponUsageTrackQuery = new CouponUsageTrackQuery(userId,coupon.getId().getValue());
        // when
        CouponUsageTrackQueryResponse response = couponApplicationService
                .trackCouponUsage(couponUsageTrackQuery);
        // then
        Mockito.verify(couponRepositoryPort).findCouponUsageByUserIdAndCouponId(userId, coupon.getId().getValue());
        Assertions.assertNotNull(response);
        Assertions.assertEquals(couponUsage.getId().getValue(), response.getCouponUsageId());
        Assertions.assertEquals(coupon.getId().getValue(), response.getCouponId());
        Assertions.assertEquals(couponUsage.getCouponId().getValue(), response.getCouponId());
    }

    @Disabled
    @Test
    @DisplayName("쿠폰이 취소되었지만 다시 쿠폰을 사용할 수 있게하는 테스트")
    public void reactiveCancelledCouponTest() {
    }
}
