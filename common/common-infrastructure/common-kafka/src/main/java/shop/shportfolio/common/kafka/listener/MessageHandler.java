package shop.shportfolio.common.kafka.listener;

import org.apache.avro.specific.SpecificRecordBase;

import java.util.List;

public interface MessageHandler<T extends SpecificRecordBase> {
    void handle(List<T> messaging, List<String> key);
}
