package shop.shportfolio.portfolio.application.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.dto.payment.PaymentPayRequest;
import shop.shportfolio.portfolio.application.command.create.DepositCreateCommand;
import shop.shportfolio.portfolio.application.command.create.DepositCreatedResponse;
import shop.shportfolio.portfolio.application.command.create.PortfolioCreatedResponse;
import shop.shportfolio.portfolio.application.command.create.WithdrawalCreatedResponse;
import shop.shportfolio.portfolio.application.command.track.*;
import shop.shportfolio.portfolio.application.dto.TotalBalanceContext;
import shop.shportfolio.portfolio.domain.entity.*;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PortfolioDataMapper {


    public CryptoBalanceTrackQueryResponse cryptoBalanceToCryptoBalanceTrackQueryResponse(
            CryptoBalance balance) {
        return new CryptoBalanceTrackQueryResponse(balance.getId().getValue(),
                balance.getMarketId().getValue(), balance.getQuantity().getValue(),
                balance.getPurchasePrice().getValue(),
                balance.getUpdatedAt().getValue());
    }

    public PortfolioTrackQueryResponse PortfolioToTotalAssetValueTrackQueryResponse(
            Portfolio portfolio) {
        return new PortfolioTrackQueryResponse(portfolio.getId().getValue(),
                portfolio.getUserId().getValue()                 , portfolio.getUpdatedAt().getValue());
    }

    public CurrencyBalanceTrackQueryResponse currencyBalanceToCurrencyBalanceTrackQueryResponse(
            CurrencyBalance currencyBalance) {
        return new CurrencyBalanceTrackQueryResponse(currencyBalance.getId().getValue(),
                currencyBalance.getAmount().getValue().longValue(), currencyBalance.getUpdatedAt().getValue());
    }

    public PortfolioCreatedResponse portfolioToPortfolioCreatedResponse(Portfolio portfolio) {
        return new PortfolioCreatedResponse(portfolio.getId().getValue(),portfolio.getUserId().getValue(),
                 portfolio.getCreatedAt().getValue());
    }

    public PaymentPayRequest depositCreateCommandToPaymentPayRequest(DepositCreateCommand command) {
        return new PaymentPayRequest(Long.parseLong(command.getAmount()), command.getOrderId(), command.getPaymentKey());
    }

    public DepositCreatedResponse currencyBalanceToDepositCreatedResponse(CurrencyBalance balance,
                                                                          UUID userId, Long amount) {
        return new DepositCreatedResponse(balance.getPortfolioId().getValue(), balance.getId().getValue(),
                userId, amount, balance.getUpdatedAt().getValue());
    }

    public WithdrawalCreatedResponse currencyBalanceToWithdrawalCreatedResponse(DepositWithdrawal depositWithdrawal,
                                                                                String message) {
        return new WithdrawalCreatedResponse(depositWithdrawal.getUserId().getValue(),
                depositWithdrawal.getAmount().getValue().longValue(),
                depositWithdrawal.getUpdatedAt().getValue(), message);
    }

    public TotalBalanceTrackQueryResponse totalBalanceContextToTotalBalanceTrackQueryResponse(
            TotalBalanceContext context) {
        return new TotalBalanceTrackQueryResponse(this.
                currencyBalanceToCurrencyBalanceTrackQueryResponse(context.getCurrencyBalance()),
                context.getCryptoBalances().stream()
                        .map(this::cryptoBalanceToCryptoBalanceTrackQueryResponse)
                        .collect(Collectors.toList()));
    }

    public AssetChangLogTrackQueryResponse assetChangLogToAssetChangLogTrackQueryResponse(AssetChangeLog assetChangLog) {
        return new AssetChangLogTrackQueryResponse(assetChangLog.getUserId().getValue(), assetChangLog.getChangeType()
                , assetChangLog.getMarketId().getValue(), assetChangLog.getChangeMoney().getValue(),
                assetChangLog.getDescription().getValue(), assetChangLog.getCreatedAt().getValue());
    }


}
