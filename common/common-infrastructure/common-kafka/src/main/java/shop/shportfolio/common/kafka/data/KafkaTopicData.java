package shop.shportfolio.common.kafka.data;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class KafkaTopicData {

    @Value("${kafka-coupon-used-trading-topic")
    private String couponCouponUsedToTradingTopic;
    @Value("${kafka-coupon-expired-trading-topic}")
    private String couponCouponExpiredTradingTopic;

    @Value("${kafka-trading-trade-portfolio-topic}")
    private String tradingTradeRecordToPortfolioTopic;

    @Value("${kafka-trading-userbalance-portfolio-topic")
    private String tradingUserBalanceToPortfolioTopic;
}
