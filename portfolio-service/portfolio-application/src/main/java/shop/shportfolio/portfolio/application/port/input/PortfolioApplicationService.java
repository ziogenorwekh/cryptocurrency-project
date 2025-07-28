package shop.shportfolio.portfolio.application.port.input;

import shop.shportfolio.portfolio.application.command.create.*;
import shop.shportfolio.portfolio.application.command.track.*;

public interface PortfolioApplicationService {

    CryptoBalanceTrackQueryResponse trackCryptoBalance(CryptoBalanceTrackQuery cryptoBalanceTrackQuery);

    CurrencyBalanceTrackQueryResponse trackCurrencyBalance(CurrencyBalanceTrackQuery currencyBalanceTrackQuery);

    TotalAssetValueTrackQueryResponse trackTotalAssetValue(TotalAssetValueTrackQuery totalAssetValueTrackQuery);

    DepositCreatedResponse deposit(DepositCreateCommand depositCreateCommand);

    PortfolioCreatedResponse createPortfolio(PortfolioCreateCommand portfolioCreateCommand);

    WithdrawalCreatedResponse withdrawal(WithdrawalCreateCommand withdrawalCreateCommand);
}
