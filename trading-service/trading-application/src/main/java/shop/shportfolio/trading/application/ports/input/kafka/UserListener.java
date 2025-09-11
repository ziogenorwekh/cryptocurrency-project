package shop.shportfolio.trading.application.ports.input.kafka;

import java.util.UUID;

public interface UserListener {
    void createUserBalance(UUID userId);
    void deleteUserBalance(UUID userId);
}
