package shop.shportfolio.common.kafka.listener;

import lombok.Getter;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.CouponAvroModel;
import shop.shportfolio.common.kafka.handler.MessageHandler;

import java.util.List;

@Getter
@Component
public class TradingCouponCommonListener {

    private final MessageHandler<CouponAvroModel> handler;

    public TradingCouponCommonListener(MessageHandler<CouponAvroModel> handler) {
        this.handler = handler;
    }

    @org.springframework.kafka.annotation.KafkaListener(
            groupId = "trading-listener-group",
            topics = "${kafka.topic.coupon}")
    public void receive(List<CouponAvroModel> messaging, List<String> key) {
        handler.handle(messaging, key);
    }
}
