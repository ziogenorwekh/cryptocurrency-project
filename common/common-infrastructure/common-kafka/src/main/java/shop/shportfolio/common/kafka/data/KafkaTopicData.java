package shop.shportfolio.common.kafka.data;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class KafkaTopicData {



    @Value("${kafka.depositwithdrawal.command.topic}")
    private String depositWithdrawalCommandTopic;
    @Value("${kafka.depositwithdrawal.event.topic}")
    private String depositWithdrawalEventTopic;


    @Value("${kafka.coupon.command.topic}")
    private String couponCommandTopic;


    @Value("${kafka.userbalance.command.topic}")
    private String userBalanceCommandTopic;
    @Value("${kafka.crypto.command.topic}")
    private String cryptoCommandTopic;


    @Value("${kafka.limitorder.command.topic}")
    private String limitOrderCommandTopic;
    @Value("${kafka.reservationorder.command.topic}")
    private String reservationOrderCommandTopic;
    @Value("${kafka.marketorder.command.topic}")
    private String marketOrderCommandTopic;
    @Value("${kafka.predicated.event.topic}")
    private String predicatedEventTopic;

    @Value("${kafka.cancelorder.command.topic}")
    private String cancelOrderCommandTopic;
    @Value("${kafka.cancelorder.event.topic}")
    private String cancelOrderEventTopic;

    @Value("${kafka.trade.command.topic}")
    private String tradeCommandTopic;

    @Value("${kafka.user.command.topic}")
    private String userCommandTopic;
}
