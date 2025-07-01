package shop.shportfolio.coupon.domain.test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.shportfoilo.coupon.domain.CouponDomainService;
import shop.shportfoilo.coupon.domain.CouponDomainServiceImpl;
import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfoilo.coupon.domain.exception.CouponDomainException;
import shop.shportfoilo.coupon.domain.valueobject.CouponCode;
import shop.shportfoilo.coupon.domain.valueobject.Discount;
import shop.shportfoilo.coupon.domain.valueobject.ExpiryDate;
import shop.shportfoilo.coupon.domain.valueobject.OwnerId;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
public class CouponDomainTest {

    private final CouponDomainService couponDomainService = new CouponDomainServiceImpl();
    private final UUID owner = UUID.randomUUID();
    private final Integer discount = 20;
    private final CouponCode couponCode = CouponCode.generate();


    @BeforeEach
    public void setUp() {

    }

    @Test
    @DisplayName("쿠폰 발급하는 테스트")
    public void createCouponTest() {
        // given && when
        Coupon coupon = couponDomainService.createCoupon(new OwnerId(owner), new Discount(discount),
                new ExpiryDate(LocalDate.now().plusDays(5)), couponCode);
        // then
        Assertions.assertNotNull(coupon);
        Assertions.assertNotNull(coupon.getId());
        Assertions.assertNotNull(coupon.getIssuedAt().getValue());
        Assertions.assertEquals(LocalDate.now().plusDays(5L), coupon.getExpiryDate().getValue());
        System.out.println("coupon.getCouponCode().getValue() = " + coupon.getCouponCode().getValue());
    }

    @Test
    @DisplayName("잘못된 할인율로 인해 쿠폰 발급이 안되는 테스트")
    public void wrongCreateCouponByDiscountTest() {
        // given && when
        CouponDomainException couponDomainException = Assertions.assertThrows(CouponDomainException.class, () -> {
            couponDomainService.createCoupon(new OwnerId(owner), new Discount(0),
                    new ExpiryDate(LocalDate.now().plusDays(5)), couponCode);
        });
        // then
        Assertions.assertNotNull(couponDomainException);
        Assertions.assertEquals("Discount cannot be zero.", couponDomainException.getMessage());
    }

    @Test
    @DisplayName("발급한 쿠폰을 사용하는 테스트")
    public void useCouponTest() {
        // given && when
    }
}
