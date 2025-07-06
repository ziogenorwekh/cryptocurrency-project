package shop.shportfolio.common.kafka.data;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class KafkaTopicData {

    @Value("${kafka-coupon-trading-topic")
    private String couponToTradingTopic;
}
