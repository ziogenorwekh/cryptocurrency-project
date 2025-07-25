package shop.shportfolio.portfolio.application.port.input;

import shop.shportfolio.portfolio.application.command.MarketBalanceTrackQuery;
import shop.shportfolio.portfolio.application.command.MarketBalanceTrackQueryResponse;
import shop.shportfolio.portfolio.application.command.UserBalanceTrackQuery;
import shop.shportfolio.portfolio.application.command.UserBalanceTrackQueryResponse;

public interface PortfolioApplicationService {

    MarketBalanceTrackQueryResponse trackMarketBalance(MarketBalanceTrackQuery marketBalanceTrackQuery);

    UserBalanceTrackQueryResponse trackUserBalance(UserBalanceTrackQuery userBalanceTrackQuery);
}
