package shop.shportfolio.portfolio.application.port.input;

import jakarta.validation.Valid;
import shop.shportfolio.portfolio.application.command.create.*;
import shop.shportfolio.portfolio.application.command.track.*;

import java.util.List;

public interface PortfolioApplicationService {

    CryptoBalanceTrackQueryResponse trackCryptoBalance(@Valid CryptoBalanceTrackQuery cryptoBalanceTrackQuery);

    CurrencyBalanceTrackQueryResponse trackCurrencyBalance(@Valid CurrencyBalanceTrackQuery currencyBalanceTrackQuery);

    PortfolioTrackQueryResponse trackPortfolio(@Valid PortfolioTrackQuery portfolioTrackQuery);

    TotalBalanceTrackQueryResponse trackTotalBalances(@Valid TotalBalanceTrackQuery totalBalanceTrackQuery);

    DepositCreatedResponse deposit(@Valid DepositCreateCommand depositCreateCommand);

    PortfolioCreatedResponse createPortfolio(@Valid PortfolioCreateCommand portfolioCreateCommand);

    WithdrawalCreatedResponse withdrawal(@Valid WithdrawalCreateCommand withdrawalCreateCommand);

    List<AssetChangLogTrackQueryResponse> trackAssetChangLog(@Valid AssetChangLogTrackQuery assetChangLogTrackQuery);
}
