package shop.shportfolio.user.application.ports.input.kafka;

public interface UserTradeRecordUseCase {
    void saveTransactionHistory(String key, String message);
}
