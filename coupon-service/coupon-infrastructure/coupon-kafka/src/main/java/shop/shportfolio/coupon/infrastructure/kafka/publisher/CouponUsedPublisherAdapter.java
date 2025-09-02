package shop.shportfolio.coupon.infrastructure.kafka.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfoilo.coupon.domain.event.CouponUsedEvent;
import shop.shportfolio.common.avro.CouponAvroModel;
import shop.shportfolio.common.kafka.data.KafkaTopicData;
import shop.shportfolio.common.kafka.publisher.KafkaPublisher;
import shop.shportfolio.coupon.application.ports.output.kafka.CouponUsedPublisher;
import shop.shportfolio.coupon.infrastructure.kafka.mapper.CouponMessageMapper;

@Slf4j
@Component
public class CouponUsedPublisherAdapter implements CouponUsedPublisher {

    private final KafkaPublisher<String, CouponAvroModel> kafkaPublisher;
    private final CouponMessageMapper couponMessageMapper;
    private final KafkaTopicData kafkaTopicData;

    @Autowired
    public CouponUsedPublisherAdapter(KafkaPublisher<String, CouponAvroModel> kafkaPublisher,
                                      CouponMessageMapper couponMessageMapper,
                                      KafkaTopicData kafkaTopicData) {
        this.kafkaPublisher = kafkaPublisher;
        this.couponMessageMapper = couponMessageMapper;
        this.kafkaTopicData = kafkaTopicData;
    }


    @Override
    public void publish(CouponUsedEvent domainEvent) {
        String couponId
                = domainEvent.getDomainType().getCouponId().getValue().toString();
        CouponAvroModel couponAvroModel = couponMessageMapper
                .couponDataToCouponAvroModel(domainEvent.getDomainType(),domainEvent.getMessageType());
        kafkaPublisher.send(kafkaTopicData.getCouponTopic(),
                couponId, couponAvroModel);
    }
}
