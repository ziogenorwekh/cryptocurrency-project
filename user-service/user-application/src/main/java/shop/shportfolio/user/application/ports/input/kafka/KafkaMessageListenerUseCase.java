package shop.shportfolio.user.application.ports.input.kafka;

public interface KafkaMessageListenerUseCase {
    void handleMessage(String key, String message);
}
