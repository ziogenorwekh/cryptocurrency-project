package shop.shportfolio.coupon.infrastructure.toss.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import shop.shportfolio.common.domain.dto.payment.PaymentPayRequest;
import shop.shportfolio.common.domain.dto.payment.PaymentResponse;
import shop.shportfolio.coupon.infrastructure.toss.client.TossAPIClient;
import shop.shportfolio.coupon.infrastructure.toss.config.WebClientConfig;
import shop.shportfolio.coupon.infrastructure.toss.config.WebClientConfigData;
import shop.shportfolio.coupon.infrastructure.toss.mapper.CouponDataApiMapper;

@SpringBootTest(classes = {
        TossAPIClient.class,
        WebClientConfig.class,
        WebClientConfigData.class,
        CouponDataApiMapper.class,
        com.fasterxml.jackson.databind.ObjectMapper.class  // 또는 jackson auto config
})
@ActiveProfiles("test")
public class TossApiTest {

    @Autowired
    private TossAPIClient tossAPIClient;


    @Disabled
    @Test
    @DisplayName("toss에서 제공하는 키로 테스트")
    public void tossApiTest() {
        // given
        PaymentPayRequest paymentPayRequest = new PaymentPayRequest(50000,"sandbox",
                "sandbox");
        // when
        PaymentResponse pay = tossAPIClient.pay(paymentPayRequest);
        // then
        Assertions.assertNotNull(pay);
        System.out.println("pay.toString() = " + pay.toString());
    }
}
