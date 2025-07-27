package shop.shportfolio.portfolio.application.port.input;

import shop.shportfolio.portfolio.application.command.*;

public interface PortfolioApplicationService {

    MarketBalanceTrackQueryResponse trackMarketBalance(MarketBalanceTrackQuery marketBalanceTrackQuery);

    UserBalanceTrackQueryResponse trackUserBalance(UserBalanceTrackQuery userBalanceTrackQuery);

    TotalAssetValueTrackQueryResponse trackTotalAssetValue(TotalAssetValueTrackQuery totalAssetValueTrackQuery);

    DepositCreatedResponse deposit(DepositCreateCommand depositCreateCommand);

    PortfolioCreatedResponse createPortfolio(PortfolioCreateCommand portfolioCreateCommand);

}
