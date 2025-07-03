package shop.shportfolio.trading.application.command.delete;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CancelLimitOrderCommand {

    private String orderId;
    private UUID userId;
    private String marketId;
}
