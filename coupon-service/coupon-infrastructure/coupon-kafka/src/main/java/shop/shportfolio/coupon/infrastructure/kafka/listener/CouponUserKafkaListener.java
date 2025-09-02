package shop.shportfolio.coupon.infrastructure.kafka.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.MessageType;
import shop.shportfolio.common.avro.UserIdAvroModel;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.common.kafka.listener.MessageHandler;
import shop.shportfolio.coupon.application.ports.input.kafka.CouponUserListener;

import java.util.List;
import java.util.UUID;

@Component
public class CouponUserKafkaListener implements MessageHandler<UserIdAvroModel> {

    private final CouponUserListener couponUserListener;

    @Autowired
    public CouponUserKafkaListener(CouponUserListener couponUserListener) {
        this.couponUserListener = couponUserListener;
    }

    @Override
    @KafkaListener(groupId = "coupon-group", topics = "${kafka.user.topic}")
    public void handle(List<UserIdAvroModel> messaging, List<String> key) {
        messaging.forEach(userId -> {
            if (userId.getMessageType().equals(MessageType.DELETE)) {
                couponUserListener.deleteCoupon(new UserId(UUID.fromString(userId.getUserId())));
            }
        });
    }
}
