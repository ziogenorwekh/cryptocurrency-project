package shop.shportfolio.trading.infrastructure.kafka.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.CouponAvroModel;
import shop.shportfolio.common.avro.MessageType;
import shop.shportfolio.common.kafka.listener.MessageHandler;
import shop.shportfolio.trading.application.dto.coupon.CouponKafkaResponse;
import shop.shportfolio.trading.application.ports.input.kafka.CouponExpiredListener;
import shop.shportfolio.trading.infrastructure.kafka.mapper.TradingMessageMapper;

import java.util.List;

@Component
public class TradingCouponExpiredListener implements MessageHandler<CouponAvroModel> {

    private final CouponExpiredListener couponExpiredListener;
    private final TradingMessageMapper tradingMessageMapper;

    @Autowired
    public TradingCouponExpiredListener(CouponExpiredListener couponExpiredListener,
                                        TradingMessageMapper tradingMessageMapper) {
        this.couponExpiredListener = couponExpiredListener;
        this.tradingMessageMapper = tradingMessageMapper;
    }

    @Override
    @KafkaListener(groupId = "trading-listener-group", topics = "${kafka.coupon.expired.trading.topic}")
    public void handle(List<CouponAvroModel> messaging, List<String> key) {
        messaging.forEach(message -> {
            if (message.getMessageType().equals(MessageType.DELETE)) {
                CouponKafkaResponse response = tradingMessageMapper
                        .couponResponseToCouponAvroModel(message);
                couponExpiredListener.deleteCoupon(response);
            }
        });
    }
}