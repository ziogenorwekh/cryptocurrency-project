package shop.shportfolio.trading.infrastructure.kafka.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.MessageType;
import shop.shportfolio.common.avro.UserIdAvroModel;
import shop.shportfolio.common.kafka.listener.MessageHandler;
import shop.shportfolio.trading.application.ports.input.kafka.UserListener;

import java.util.List;
import java.util.UUID;

@Component
public class UserKafkaListener implements MessageHandler<UserIdAvroModel> {

    private final UserListener userListener;

    public UserKafkaListener(UserListener userListener) {
        this.userListener = userListener;
    }

    @Override
    @KafkaListener(groupId = "trading-group", topics = "${kafka.user.topic}")
    public void handle(List<UserIdAvroModel> messaging, List<String> key) {
        messaging.forEach(record -> {
            if (record.getMessageType().equals(MessageType.CREATE)) {
                userListener.createUserBalance(UUID.fromString(record.getUserId()));
            } else if (record.getMessageType().equals(MessageType.DELETE)) {
                userListener.deleteUserBalance(UUID.fromString(record.getUserId()));
            }
        });
    }
}
