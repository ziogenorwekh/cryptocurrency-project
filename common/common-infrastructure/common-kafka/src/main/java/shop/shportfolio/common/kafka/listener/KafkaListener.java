package shop.shportfolio.common.kafka.listener;

import org.apache.avro.specific.SpecificRecordBase;

import java.util.List;

public interface KafkaListener<T extends SpecificRecordBase> {

    void receive(List<T> messaging, List<String> key);
}
