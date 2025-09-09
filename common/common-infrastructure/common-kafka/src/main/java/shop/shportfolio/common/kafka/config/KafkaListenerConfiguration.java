package shop.shportfolio.common.kafka.config;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import shop.shportfolio.common.kafka.data.KafkaConfigData;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaListenerConfiguration<K extends Serializable,V extends SpecificRecordBase> {

    private final KafkaConfigData kafkaConfigData;

    public KafkaListenerConfiguration(KafkaConfigData kafkaConfigData) {
        this.kafkaConfigData = kafkaConfigData;
    }

    @Bean
    public ConsumerFactory<K, V> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers());
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        configProps.put("schema.registry.url", kafkaConfigData.getSchemaRegistryUrl());
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfigData.getGroupId());
        return new DefaultKafkaConsumerFactory<>(configProps);
    }
}
