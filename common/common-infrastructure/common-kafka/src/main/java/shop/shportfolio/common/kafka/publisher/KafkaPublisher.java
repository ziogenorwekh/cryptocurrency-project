package shop.shportfolio.common.kafka.publisher;

import org.apache.avro.specific.SpecificRecordBase;

import java.io.Serializable;

public interface KafkaPublisher<K extends Serializable, V extends SpecificRecordBase> {
    void send(String topicName, K key, V messaging);
}