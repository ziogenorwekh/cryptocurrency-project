package shop.shportfolio.common.kafka.data;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class KafkaTopicData {

    @Value("${kafka.limitorder.topic}")
    private String limitOrderTopic;
    @Value("${kafka.reservationorder.topic}")
    private String reservationOrderTopic;
    @Value("${kafka.marketorder.topic}")
    private String marketOrderTopic;

    @Value("${kafka.predicated.topic}")
    private String predicatedTradeTopic;
    @Value("${kafka.trade.topic}")
    private String tradeTopic;
    @Value("${kafka.depositwithdrawal.topic}")
    private String depositWithdrawalTopic;
    @Value("${kafka.coupon.topic}")
    private String couponTopic;
    @Value("${kafka.userbalance.topic}")
    private String userBalanceTopic;
    @Value("${kafka.user.topic}")
    private String userTopic;
    @Value("${kafka.order.topic}")
    private String orderTopic;
}
