package shop.shportfolio.trading.application.command.create;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.shportfolio.trading.domain.valueobject.OrderPrice;
import shop.shportfolio.trading.domain.valueobject.OrderSide;
import shop.shportfolio.trading.domain.valueobject.OrderType;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateLimitOrderCommand {
    private UUID userId;
    private String marketId;
    private BigDecimal marketItemTick;
    private BigDecimal price;
    private String orderSide;
    private BigDecimal quantity;
    private String orderType;


}
