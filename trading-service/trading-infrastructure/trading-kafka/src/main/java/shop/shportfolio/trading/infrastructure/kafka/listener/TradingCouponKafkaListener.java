package shop.shportfolio.trading.infrastructure.kafka.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.CouponAvroModel;
import shop.shportfolio.common.avro.MessageType;
import shop.shportfolio.common.kafka.listener.MessageHandler;
import shop.shportfolio.trading.application.dto.coupon.CouponKafkaResponse;
import shop.shportfolio.trading.application.ports.input.kafka.CouponListener;
import shop.shportfolio.trading.infrastructure.kafka.mapper.TradingMessageMapper;

import java.util.List;

@Component
public class TradingCouponKafkaListener implements MessageHandler<CouponAvroModel> {

    private final CouponListener couponListener;
    private final TradingMessageMapper tradingMessageMapper;

    public TradingCouponKafkaListener(CouponListener couponListener,
                                      TradingMessageMapper tradingMessageMapper) {
        this.couponListener = couponListener;
        this.tradingMessageMapper = tradingMessageMapper;
    }

    @Override
    @KafkaListener(groupId = "trading-group", topics = "${kafka.coupon.topic}")
    public void handle(List<CouponAvroModel> messaging, List<String> key) {
        messaging.forEach(couponAvroModel -> {
            CouponKafkaResponse couponKafkaResponse = tradingMessageMapper.couponResponseToCouponAvroModel(couponAvroModel);
            switch (couponAvroModel.getMessageType()) {
                case CREATE -> couponListener.saveCoupon(couponKafkaResponse);
                case DELETE -> couponListener.deleteCoupon(couponKafkaResponse);
            }
        });
    }
}
