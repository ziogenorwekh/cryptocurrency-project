package shop.shportfolio.coupon.application.test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfoilo.coupon.domain.exception.CouponDomainException;
import shop.shportfoilo.coupon.domain.valueobject.*;
import shop.shportfolio.common.domain.valueobject.RoleType;
import shop.shportfolio.coupon.application.command.create.CouponCreateCommand;
import shop.shportfolio.coupon.application.command.create.CouponCreatedResponse;
import shop.shportfolio.coupon.application.command.track.CouponListTrackQuery;
import shop.shportfolio.coupon.application.command.track.CouponTrackQuery;
import shop.shportfolio.coupon.application.command.track.CouponTrackQueryResponse;
import shop.shportfolio.coupon.application.command.update.CouponUseUpdateCommand;
import shop.shportfolio.coupon.application.command.update.CouponUseUpdateResponse;
import shop.shportfolio.coupon.application.dto.payment.Payment;
import shop.shportfolio.coupon.application.dto.payment.PaymentStatus;
import shop.shportfolio.coupon.application.policy.CouponDiscountPolicy;
import shop.shportfolio.coupon.application.policy.ExpireAtPolicy;
import shop.shportfolio.coupon.application.ports.input.CouponApplicationService;
import shop.shportfolio.coupon.application.ports.output.payment.PaymentPort;
import shop.shportfolio.coupon.application.ports.output.repository.CouponRepositoryAdapter;
import shop.shportfolio.coupon.application.test.mockbean.CouponMockBean;

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
    private CouponRepositoryAdapter couponRepositoryAdapter;

    @Autowired
    private CouponDiscountPolicy couponDiscountPolicy;

    @Autowired
    private ExpireAtPolicy expireAtPolicy;

    @Autowired
    private PaymentPort paymentPort;

    private final UUID userId = UUID.randomUUID();
    List<RoleType> roleTypeWithUserAndSilver;
    List<Coupon> couponList;

    @BeforeEach
    public void setUp() {
        couponList = new ArrayList<>();
        roleTypeWithUserAndSilver = new ArrayList<>();


        roleTypeWithUserAndSilver.add(RoleType.USER);
        roleTypeWithUserAndSilver.add(RoleType.SILVER);
        couponList.add(Coupon.createCoupon(new OwnerId(userId),
                couponDiscountPolicy.calculatorDiscount(roleTypeWithUserAndSilver),
                expireAtPolicy.calculate(roleTypeWithUserAndSilver),
                CouponCode.generate()));
        couponList.add(Coupon.createCoupon(
                new OwnerId(userId), couponDiscountPolicy.calculatorDiscount(roleTypeWithUserAndSilver),
                expireAtPolicy.calculate(roleTypeWithUserAndSilver), CouponCode.generate()));
    }

    @Test
    @DisplayName("쿠폰 생성 테스트")
    public void createCouponTest() {
        // given
        ExpiryDate expiryDate = expireAtPolicy.calculate(roleTypeWithUserAndSilver);
        FeeDiscount feeDiscount = couponDiscountPolicy.calculatorDiscount(roleTypeWithUserAndSilver);
        CouponCode couponCode = CouponCode.generate();
        CouponCreateCommand createCommand = new CouponCreateCommand(userId, roleTypeWithUserAndSilver);
        Coupon mockCoupon = Coupon.createCoupon(new OwnerId(userId),
                feeDiscount,
                expiryDate,
                couponCode);
        Mockito.when(couponRepositoryAdapter.save(Mockito.any()))
                .thenReturn(mockCoupon);
        Mockito.when(paymentPort.pay()).thenReturn(new Payment(null,null,
                null,null,null,null,
                null,null, 5000L, PaymentStatus.DONE,
                null,null,null,null,null));
        // when
        CouponCreatedResponse couponCreatedResponse = couponApplicationService.createCoupon(createCommand);
        // then
        Mockito.verify(paymentPort, Mockito.times(1)).pay();
        Assertions.assertNotNull(couponCreatedResponse);
        Assertions.assertEquals(userId, couponCreatedResponse.getOwner());
        Assertions.assertEquals(couponCode.getValue(), couponCreatedResponse.getCouponCode());
        Assertions.assertEquals(feeDiscount.getValue(), couponCreatedResponse.getFeeDiscount());
        Assertions.assertEquals(expiryDate.getValue(), couponCreatedResponse.getExpiryDate());
    }

    @Test
    @DisplayName("쿠폰 리스트 조회 테스트")
    public void trackCouponListTest() {
        // given
        CouponListTrackQuery couponListTrackQuery = new CouponListTrackQuery(userId);
        Mockito.when(couponRepositoryAdapter.findByUserId(userId)).thenReturn(couponList);
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
        Coupon coupon = Coupon.createCoupon(new OwnerId(userId),
                couponDiscountPolicy.calculatorDiscount(roleTypeWithUserAndSilver),
                expireAtPolicy.calculate(roleTypeWithUserAndSilver),
                CouponCode.generate());
        CouponTrackQuery couponTrackQuery = new CouponTrackQuery(userId, coupon.getId().getValue());
        Mockito.when(couponRepositoryAdapter.findByUserIdAndCouponId(userId, coupon.getId().getValue()))
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
        Coupon coupon = Coupon.createCoupon(new OwnerId(userId),
                couponDiscountPolicy.calculatorDiscount(roleTypeWithUserAndSilver),
                expireAtPolicy.calculate(roleTypeWithUserAndSilver),
                generate);
        Mockito.when(couponRepositoryAdapter.findByUserIdAndCouponId(userId, coupon.getId().getValue()))
                .thenReturn(Optional.of(coupon));
        CouponUseUpdateCommand couponUseUpdateCommand = new CouponUseUpdateCommand(userId, coupon.getId().getValue(),
                generate.getValue());
        Coupon savedCoupon = new Coupon(coupon.getId(), coupon.getOwner(), coupon.getFeeDiscount(), coupon.getExpiryDate()
                , coupon.getIssuedAt(), coupon.getCouponCode(), CouponStatus.USED);
        Mockito.when(couponRepositoryAdapter.save(coupon)).thenReturn(savedCoupon);
        // when
        CouponUseUpdateResponse couponUseUpdateResponse = couponApplicationService.useCoupon(couponUseUpdateCommand);
        // then
        Assertions.assertNotNull(couponUseUpdateResponse);
        Assertions.assertEquals(userId, couponUseUpdateResponse.getOwner());
        Assertions.assertEquals(coupon.getId().getValue(), couponUseUpdateResponse.getCouponId());
        Assertions.assertEquals(coupon.getCouponCode().getValue(), couponUseUpdateResponse.getCouponCode());
        Assertions.assertEquals(coupon.getFeeDiscount().getValue(), couponUseUpdateResponse.getFeeDiscount());
        Assertions.assertEquals(CouponStatus.USED, couponUseUpdateResponse.getStatus());
    }

    @Test
    @DisplayName("쿠폰이 만료되었는데 사용할려면 에러를 뱉어야 하는 테스트")
    public void expiredCouponUsingTest() {
        // given
        CouponCode generate = CouponCode.generate();
        Coupon coupon = Coupon.createCoupon(new OwnerId(userId),
                couponDiscountPolicy.calculatorDiscount(roleTypeWithUserAndSilver),
                expireAtPolicy.calculate(roleTypeWithUserAndSilver),
                generate);
        Coupon usedCoupon = new Coupon(coupon.getId(), coupon.getOwner(), coupon.getFeeDiscount(),
                new ExpiryDate(coupon.getExpiryDate().getValue().minusDays(3))
                , coupon.getIssuedAt(), coupon.getCouponCode(), CouponStatus.EXPIRED);
        Mockito.when(couponRepositoryAdapter.findByUserIdAndCouponId(userId, coupon.getId().getValue())).thenReturn(
                Optional.of(usedCoupon));
        CouponUseUpdateCommand couponUseUpdateCommand = new CouponUseUpdateCommand(userId, coupon.getId().getValue(),
                generate.getValue());
        // when
        CouponDomainException couponDomainException = Assertions.assertThrows(CouponDomainException.class,
                () -> couponApplicationService.useCoupon(couponUseUpdateCommand));
        // then
        Assertions.assertNotNull(couponDomainException);
        Assertions.assertEquals("Coupon is not active and cannot be used.",couponDomainException.getMessage());
    }

    @Test
    @DisplayName("쿠폰을 취소하는 테스트")
    public void cancelCouponTest() {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("쿠폰이 취소되었지만 다시 쿠폰을 사용할 수 있게하는 테스트")
    public void reactiveCancelledCouponTest() {
        // given
        CouponCode generate = CouponCode.generate();
        Coupon coupon = Coupon.createCoupon(new OwnerId(userId),
                couponDiscountPolicy.calculatorDiscount(roleTypeWithUserAndSilver),
                expireAtPolicy.calculate(roleTypeWithUserAndSilver),
                generate);
        Coupon cancelledCoupon = new Coupon(coupon.getId(), coupon.getOwner(), coupon.getFeeDiscount(),
                coupon.getExpiryDate()
                , coupon.getIssuedAt(), coupon.getCouponCode(), CouponStatus.USED);
        Mockito.when(couponRepositoryAdapter.findByUserIdAndCouponId(userId, coupon.getId().getValue()))
                .thenReturn(Optional.of(cancelledCoupon));
        // when

        // then
    }
}
