package shop.shportfolio.trading.infrastructure.kafka.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.CouponAvroModel;
import shop.shportfolio.common.kafka.handler.MessageHandler;
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
    public void handle(List<CouponAvroModel> messaging, List<String> key) {
        messaging.forEach(couponAvroModel -> {
            CouponKafkaResponse couponKafkaResponse = tradingMessageMapper.
                    couponResponseToCouponAvroModel(couponAvroModel);
            couponExpiredListener.deleteCoupon(couponKafkaResponse);
        });
    }
}
