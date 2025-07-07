package shop.shportfolio.trading.infrastructure.kafka.listener;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.CouponAvroModel;
import shop.shportfolio.common.kafka.handler.MessageHandler;
import shop.shportfolio.trading.application.dto.coupon.CouponKafkaResponse;
import shop.shportfolio.trading.application.ports.input.kafka.CouponAppliedListener;
import shop.shportfolio.trading.infrastructure.kafka.mapper.TradingMessageMapper;

import java.util.List;

@Component
public class TradingListener implements MessageHandler<CouponAvroModel> {

    private final CouponAppliedListener couponAppliedListener;
    private final TradingMessageMapper tradingMessageMapper;
    public TradingListener(CouponAppliedListener couponAppliedListener,
                           TradingMessageMapper tradingMessageMapper) {
        this.couponAppliedListener = couponAppliedListener;
        this.tradingMessageMapper = tradingMessageMapper;
    }

    @Override
    public void handle(List<CouponAvroModel> messaging, List<String> key) {
        messaging.forEach(couponAvroModel -> {
            CouponKafkaResponse couponKafkaResponse = tradingMessageMapper.couponResponseToCouponAvroModel(couponAvroModel);
            couponAppliedListener.saveCoupon(couponKafkaResponse);
        });
    }
}
