package shop.shportfolio.coupon.infrastructure.kafka.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfoilo.coupon.domain.event.CouponExpiredEvent;
import shop.shportfolio.common.avro.CouponAvroModel;
import shop.shportfolio.common.kafka.data.KafkaTopicData;
import shop.shportfolio.common.kafka.publisher.KafkaPublisher;
import shop.shportfolio.coupon.application.ports.output.kafka.CouponExpiredPublisher;
import shop.shportfolio.coupon.infrastructure.kafka.mapper.CouponMessageMapper;

@Component
public class CouponExpiredPublisherAdapter implements CouponExpiredPublisher {

    private final KafkaPublisher<String, CouponAvroModel> kafkaPublisher;
    private final CouponMessageMapper couponMessageMapper;
    private final KafkaTopicData kafkaTopicData;

    @Autowired
    public CouponExpiredPublisherAdapter(KafkaPublisher<String, CouponAvroModel> kafkaPublisher,
                                         CouponMessageMapper couponMessageMapper,
                                         KafkaTopicData kafkaTopicData) {
        this.kafkaPublisher = kafkaPublisher;
        this.couponMessageMapper = couponMessageMapper;
        this.kafkaTopicData = kafkaTopicData;
    }

    @Override
    public void publish(CouponExpiredEvent domainEvent) {
        String couponId = domainEvent.getDomainType().getId().getValue().toString();
        CouponAvroModel couponAvroModel = couponMessageMapper
                .couponToCouponAvroModel(domainEvent.getDomainType(),domainEvent.getMessageType());
        kafkaPublisher.send(kafkaTopicData.getCouponCouponExpiredTradingTopic(),
                couponId, couponAvroModel);
    }
}
