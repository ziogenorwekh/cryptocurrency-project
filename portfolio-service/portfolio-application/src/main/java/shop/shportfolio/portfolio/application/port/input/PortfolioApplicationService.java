package shop.shportfolio.portfolio.application.port.input;

import shop.shportfolio.portfolio.application.command.create.*;
import shop.shportfolio.portfolio.application.command.track.*;

import java.util.List;

public interface PortfolioApplicationService {

    CryptoBalanceTrackQueryResponse trackCryptoBalance(CryptoBalanceTrackQuery cryptoBalanceTrackQuery);

    CurrencyBalanceTrackQueryResponse trackCurrencyBalance(CurrencyBalanceTrackQuery currencyBalanceTrackQuery);

    PortfolioTrackQueryResponse trackPortfolio(PortfolioTrackQuery portfolioTrackQuery);

    TotalBalanceTrackQueryResponse trackTotalBalances(TotalBalanceTrackQuery totalBalanceTrackQuery);

    DepositCreatedResponse deposit(DepositCreateCommand depositCreateCommand);

    PortfolioCreatedResponse createPortfolio(PortfolioCreateCommand portfolioCreateCommand);

    WithdrawalCreatedResponse withdrawal(WithdrawalCreateCommand withdrawalCreateCommand);

    List<AssetChangLogTrackQueryResponse> trackAssetChangLog(AssetChangLogTrackQuery assetChangLogTrackQuery);
}
