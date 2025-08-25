package shop.shportfolio.common.kafka.data;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class KafkaTopicData {

    @Value("${kafka.coupon.used.trading.topic}")
    private String couponUsedToTradingTopic;
    @Value("${kafka.coupon.expired.trading.topic}")
    private String couponExpiredToTradingTopic;
    @Value("${kafka.trading.record.portfolio.topic}")
    private String tradingRecordToPortfolioTopic;
    @Value("${kafka.trading.userbalance.portfolio.topic}")
    private String tradingUserBalanceToPortfolioTopic;
    @Value("${kafka.portfolio.depositwithdrawal.trading.topic}")
    private String portfolioDepositWithdrawalToTradingTopic;
}
