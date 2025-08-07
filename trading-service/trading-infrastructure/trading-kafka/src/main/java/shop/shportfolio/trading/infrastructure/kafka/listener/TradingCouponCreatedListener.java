package shop.shportfolio.trading.infrastructure.kafka.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.CouponAvroModel;
import shop.shportfolio.common.avro.MessageType;
import shop.shportfolio.common.kafka.listener.MessageHandler;
import shop.shportfolio.trading.application.dto.coupon.CouponKafkaResponse;
import shop.shportfolio.trading.application.ports.input.kafka.CouponCreatedListener;
import shop.shportfolio.trading.infrastructure.kafka.mapper.TradingMessageMapper;

import java.util.List;

@Component
public class TradingCouponCreatedListener implements MessageHandler<CouponAvroModel> {

    private final CouponCreatedListener couponCreatedListener;
    private final TradingMessageMapper tradingMessageMapper;

    public TradingCouponCreatedListener(CouponCreatedListener couponCreatedListener,
                                        TradingMessageMapper tradingMessageMapper) {
        this.couponCreatedListener = couponCreatedListener;
        this.tradingMessageMapper = tradingMessageMapper;
    }

    @Override
    @KafkaListener(groupId = "trading-listener-group", topics = "${kafka.coupon.created.trading.topic}")
    public void handle(List<CouponAvroModel> messaging, List<String> key) {
        messaging.forEach(couponAvroModel -> {
            if (couponAvroModel.getMessageType().equals(MessageType.CREATE)) {
                CouponKafkaResponse couponKafkaResponse = tradingMessageMapper.couponResponseToCouponAvroModel(couponAvroModel);
                couponCreatedListener.saveCoupon(couponKafkaResponse);
            }
        });
    }
}
