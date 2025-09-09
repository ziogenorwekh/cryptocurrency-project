package shop.shportfolio.common.kafka.config;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;
import shop.shportfolio.common.kafka.data.KafkaConfigData;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaPublisherConfiguration<K extends Serializable, V extends SpecificRecordBase> {

    private final KafkaConfigData kafkaConfigData;

    @Autowired
    public KafkaPublisherConfiguration(KafkaConfigData kafkaConfigData) {
        this.kafkaConfigData = kafkaConfigData;
    }

    @Bean
    public ProducerFactory<K, V> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaConfigData.getKeySerializerClass()); // StringSerializer
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaConfigData.getValueSerializerClass()); // KafkaAvroSerializer
        configProps.put(ProducerConfig.ACKS_CONFIG, kafkaConfigData.getAcks());
        configProps.put(ProducerConfig.RETRIES_CONFIG, kafkaConfigData.getRetries());
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaConfigData.getBatchSize());
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, kafkaConfigData.getLingerMs());
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, kafkaConfigData.getBufferMemory());

        // Avro/Schema Registry 관련
        configProps.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaConfigData.getSchemaRegistryUrl());
        configProps.put(KafkaAvroSerializerConfig.AUTO_REGISTER_SCHEMAS, kafkaConfigData.isAutoRegisterSchemas());

//        // 인증 필요한 경우
//        if(kafkaConfigData.getSchemaRegistryUsername() != null && kafkaConfigData.getSchemaRegistryPassword() != null) {
//            configProps.put("basic.auth.credentials.source", "USER_INFO");
//            configProps.put("basic.auth.user.info",
//                    kafkaConfigData.getSchemaRegistryUsername() + ":" + kafkaConfigData.getSchemaRegistryPassword());
//        }

        return new DefaultKafkaProducerFactory<>(configProps);
    }


    @Bean
    public KafkaTemplate<K, V> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
