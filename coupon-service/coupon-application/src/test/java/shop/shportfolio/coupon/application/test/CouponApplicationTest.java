package shop.shportfolio.coupon.application.test;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import shop.shportfolio.coupon.application.test.mockbean.CouponMockBean;

@SpringBootTest(classes = {CouponMockBean.class})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
public class CouponApplicationTest {
}
