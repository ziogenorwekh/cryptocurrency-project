package shop.shportfolio.portfolio.application.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.UpdatedAt;
import shop.shportfolio.portfolio.domain.valueobject.PortfolioId;
import shop.shportfolio.portfolio.domain.valueobject.PurchasePrice;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class MarketBalanceTrackQueryResponse {
    private final UUID userBalanceId;
    private final UUID portfolioId;
    private final String marketId;
    private final BigDecimal quantity;
    private final BigDecimal purchasePrice;
    private final LocalDateTime updatedAt;

}
