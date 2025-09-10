package shop.shportfolio.coupon.domain.test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.shportfoilo.coupon.domain.CouponDomainService;
import shop.shportfoilo.coupon.domain.CouponDomainServiceImpl;
import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfoilo.coupon.domain.entity.CouponUsage;
import shop.shportfoilo.coupon.domain.exception.CouponDomainException;
import shop.shportfoilo.coupon.domain.valueobject.*;
import shop.shportfolio.common.domain.valueobject.FeeDiscount;
import shop.shportfolio.common.domain.valueobject.UsageExpiryDate;
import shop.shportfolio.common.domain.valueobject.UserId;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
public class CouponDomainTest {

    private final CouponDomainService couponDomainService = new CouponDomainServiceImpl();
    private final UUID owner = UUID.randomUUID();
    private final Integer discount = 20;
    private final CouponCode couponCode = CouponCode.generate();
    private Coupon coupon;

    @BeforeEach
    public void setUp() {
        coupon = couponDomainService.createCoupon(new UserId(owner), new FeeDiscount(discount),
                new ValidUntil(LocalDate.now(ZoneOffset.UTC)), couponCode);
    }

    @AfterEach
    public void tearDown() {
        coupon = null;
    }

    @Test
    @DisplayName("쿠폰 발급하는 테스트")
    public void createCouponTest() {
        // given && when
        Coupon coupon = couponDomainService.createCoupon(new UserId(owner), new FeeDiscount(discount),
                new ValidUntil(LocalDate.now(ZoneOffset.UTC).plusDays(5)), couponCode);
        // then
        Assertions.assertNotNull(coupon);
        Assertions.assertNotNull(coupon.getId());
        Assertions.assertNotNull(coupon.getIssuedAt().getValue());
        Assertions.assertEquals(LocalDate.now(ZoneOffset.UTC).plusDays(5L), coupon.getValidUntil().getValue());
        System.out.println("coupon.getCouponCode().getValue() = " + coupon.getCouponCode().getValue());
    }

    @Test
    @DisplayName("잘못된 할인율로 인해 쿠폰 발급이 안되는 테스트")
    public void wrongCreateCouponByDiscountTest() {
        // given && when
        CouponDomainException couponDomainException = Assertions.assertThrows(CouponDomainException.class, () -> {
            couponDomainService.createCoupon(new UserId(owner), new FeeDiscount(0),
                    new ValidUntil(LocalDate.now(ZoneOffset.UTC).plusDays(5)), couponCode);
        });
        // then
        Assertions.assertNotNull(couponDomainException);
        Assertions.assertEquals("Discount cannot be zero.", couponDomainException.getMessage());
    }

    @Test
    @DisplayName("발급한 쿠폰을 사용하는 테스트")
    public void useCouponTest() {
        // given && when
        System.out.println("coupon = " + coupon.getStatus().name());
        couponDomainService.useCoupon(coupon,couponCode.getValue());
        // then
        Assertions.assertEquals(CouponStatus.USED,coupon.getStatus());
    }

    @Test
    @DisplayName("시간이 다 되서 쿠폰을 만료하는 테스트")
    public void expireCouponTest() {
        // given
        coupon = couponDomainService.createCoupon(new UserId(owner), new FeeDiscount(discount),
                new ValidUntil(LocalDate.now(ZoneOffset.UTC).minusDays(2L)), couponCode);
        // when
        couponDomainService.updateStatusIfCouponExpired(coupon);
        // then
        Assertions.assertEquals(CouponStatus.EXPIRED,coupon.getStatus());
    }

    @Test
    @DisplayName("쿠폰을 취소하는 테스트")
    public void cancelCouponTest() {
        // given && when
        couponDomainService.cancel(coupon);
        // then
        Assertions.assertEquals(CouponStatus.CANCELED,coupon.getStatus());
    }

    @Test
    @DisplayName("만료가 된 쿠폰을 취소하려는데 에러나는 테스트")
    public void cancelExpiredCouponTest() {
        // given
        coupon = couponDomainService.createCoupon(new UserId(owner), new FeeDiscount(discount),
                new ValidUntil(LocalDate.now(ZoneOffset.UTC).minusDays(2L)), couponCode);
        couponDomainService.updateStatusIfCouponExpired(coupon);

        // when
        CouponDomainException couponDomainException = Assertions.assertThrows(CouponDomainException.class, () -> {
            couponDomainService.cancel(coupon);
        });
        // then
        Assertions.assertNotNull(couponDomainException);
        Assertions.assertEquals("Cannot cancel a coupon that is already used or expired.",
                couponDomainException.getMessage());
    }

    @Test
    @DisplayName("만료된 쿠폰을 사용하려 하면 예외 발생")
    void expiredCouponCannotBeUsed() {
        // given
        Coupon expiredCoupon = new Coupon(
                coupon.getId(),
                coupon.getOwner(),
                coupon.getFeeDiscount(),
                new ValidUntil(LocalDate.now(ZoneOffset.UTC).minusDays(1)),
                coupon.getIssuedAt(),
                coupon.getCouponCode(),
                CouponStatus.ACTIVE
        );
        // when
        CouponDomainException ex = Assertions.assertThrows(CouponDomainException.class, () -> {
            couponDomainService.useCoupon(expiredCoupon, expiredCoupon.getCouponCode().getValue());
        });
        // then
        Assertions.assertEquals("Coupon is expired.", ex.getMessage());
    }


    @Test
    @DisplayName("만료가 되거나 취소된 쿠폰을 다시 활용하는 테스트 1. 취소한 쿠폰")
    public void canceledCouponReActiveTest() {
        // given
        couponDomainService.cancel(coupon);
        // when
        couponDomainService.reactivate(coupon);
        // then
        Assertions.assertEquals(CouponStatus.ACTIVE,coupon.getStatus());
    }

    @Test
    @DisplayName("쿠폰 사용하면 CouponUsage가 나오는 테스트")
    public void createCouponUsageTest() {
        // given
        coupon.useCoupon(coupon.getCouponCode().getValue());
        UsageExpiryDate usageExpiryDate = new UsageExpiryDate(LocalDate.now(ZoneOffset.UTC).plusMonths(2));
        // when
        CouponUsage couponUsage = coupon.createCouponUsage(new UsageExpiryDate(LocalDate.now(ZoneOffset.UTC)
                .plusMonths(1)));
        // then
        Assertions.assertNotNull(couponUsage);
        Assertions.assertEquals(couponUsage.getExpiryDate(), usageExpiryDate);
    }
}
