package shop.shportfolio.coupon.infrastructure.toss.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.shportfolio.coupon.infrastructure.toss.mapper.CouponDataApiMapper;

@Configuration
public class BeanConfig {

    @Bean
    public CouponDataApiMapper couponDataApiMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        return new CouponDataApiMapper(objectMapper);
    }
}
