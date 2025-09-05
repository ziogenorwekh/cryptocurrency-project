package shop.shportfolio.trading.application.command.create;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMarketOrderCommand {

    @NotNull(message = "User ID is required.")
    private UUID userId;

    @NotBlank(message = "Market ID is required.")
    private String marketId;

    @NotBlank(message = "Order side is required.")
    @Pattern(regexp = "BUY|SELL", message = "Order side must be either BUY or SELL.")
    private String orderSide;

    @NotNull(message = "Quantity is required.")
    private BigDecimal quantity;

    @NotNull(message = "Order price is required.")
    private BigDecimal orderPrice;

    @NotBlank(message = "Order type is required.")
    @Pattern(regexp = "LIMIT|MARKET|RESERVATION", message = "Order type must be LIMIT, MARKET, or RESERVATION.")
    private String orderType;
}
