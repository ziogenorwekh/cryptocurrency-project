package shop.shportfolio.common.kafka.data;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class KafkaConfigData {
    @Value("${kafka-config.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${kafka-config.schema-registry-url}")
    private String schemaRegistryUrl;
    @Value("${kafka-config.schema-registry-username}")
    private String schemaRegistryUsername;
    @Value("${kafka-config.schema-registry-password}")
    private String schemaRegistryPassword;
    @Value("${kafka-config.auto-register-schemas}")
    private boolean autoRegisterSchemas;
    @Value("${kafka-config.acks}")
    private String acks;
    @Value("${kafka-config.retries}")
    private int retries;
    @Value("${kafka-config.batch-size}")
    private int batchSize;
    @Value("${kafka-config.linger-ms}")
    private int lingerMs;
    @Value("${kafka-config.buffer-memory}")
    private int bufferMemory;
    @Value("${kafka-config.key-serializer-class}")
    private String keySerializerClass;
    @Value("${kafka-config.value-serializer-class}")
    private String valueSerializerClass;
    @Value("${kafka-config.key-deserializer-class}")
    private String keyDeserializerClass;
    @Value("${kafka-config.value-deserializer-class}")
    private String valueDeserializerClass;
    @Value("${kafka-config.auto-offset-reset}")
    private String autoOffsetReset;
    @Value("${kafka-config.max-poll-records}")
    private int maxPollRecords;
    @Value("${kafka-config.group-id}")
    private String groupId;
    @Value("${kafka-config.specific-avro-reader}")
    private String specificAvroReader;
}
