package shop.shportfolio.common.kafka.publisher;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Slf4j
@Component
public class KafkaPublisherImpl<K extends Serializable, V extends SpecificRecordBase> implements KafkaPublisher<K, V> {


    private final KafkaTemplate<K, V> kafkaTemplate;

    @Autowired
    public KafkaPublisherImpl(KafkaTemplate<K, V> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Async
    @Override
    public void send(String topicName, K key, V messaging) {
        try {
            kafkaTemplate.send(topicName, key, messaging);
        } catch (KafkaException e) {
            log.error("kafka message send error : {}", e.getMessage());
        } catch (Exception e) {
            log.error("kafka message send error Class is : {}, error message is : {}", e.getClass(), e.getMessage());
        }
    }
}
