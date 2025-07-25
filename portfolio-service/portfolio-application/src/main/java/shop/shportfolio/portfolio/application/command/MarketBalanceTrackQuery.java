package shop.shportfolio.portfolio.application.command;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class MarketBalanceTrackQuery {
    private UUID portfolioId;
    private String marketId;
}
