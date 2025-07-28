package shop.shportfolio.portfolio.application.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.dto.payment.PaymentPayRequest;
import shop.shportfolio.portfolio.application.command.create.DepositCreateCommand;
import shop.shportfolio.portfolio.application.command.create.DepositCreatedResponse;
import shop.shportfolio.portfolio.application.command.create.PortfolioCreatedResponse;
import shop.shportfolio.portfolio.application.command.create.WithdrawalCreatedResponse;
import shop.shportfolio.portfolio.application.command.track.CryptoBalanceTrackQueryResponse;
import shop.shportfolio.portfolio.application.command.track.TotalAssetValueTrackQueryResponse;
import shop.shportfolio.portfolio.application.command.track.UserBalanceTrackQueryResponse;
import shop.shportfolio.portfolio.domain.entity.CryptoBalance;
import shop.shportfolio.portfolio.domain.entity.CurrencyBalance;
import shop.shportfolio.portfolio.domain.entity.DepositWithdrawal;
import shop.shportfolio.portfolio.domain.entity.Portfolio;

import java.util.UUID;

@Component
public class PortfolioDataMapper {


    public CryptoBalanceTrackQueryResponse balanceToCryptoBalanceTrackQueryResponse(CryptoBalance balance) {
        return new CryptoBalanceTrackQueryResponse(balance.getId().getValue(),
                balance.getPortfolioId().getValue(),
                balance.getMarketId().getValue(), balance.getQuantity().getValue(),
                balance.getPurchasePrice().getValue(),
                balance.getUpdatedAt().getValue());
    }

    public TotalAssetValueTrackQueryResponse PortfolioToTotalAssetValueTrackQueryResponse(Portfolio portfolio) {
        return new TotalAssetValueTrackQueryResponse(portfolio.getId().getValue(),
                portfolio.getUserId().getValue(), portfolio.getTotalAssetValue().getValue()
                , portfolio.getUpdatedAt().getValue());
    }

    public PortfolioCreatedResponse portfolioToPortfolioCreatedResponse(Portfolio portfolio) {
        return new PortfolioCreatedResponse(portfolio.getId().getValue(),portfolio.getUserId().getValue(),
                portfolio.getTotalAssetValue().getValue(), portfolio.getCreatedAt().getValue());
    }

    public PaymentPayRequest depositCreateCommandToPaymentPayRequest(DepositCreateCommand command) {
        return new PaymentPayRequest(command.getAmount(), command.getOrderId(), command.getPaymentKey());
    }

    public DepositCreatedResponse currencyBalanceToDepositCreatedResponse(CurrencyBalance balance,
                                                                          UUID userId, Long amount) {
        return new DepositCreatedResponse(balance.getPortfolioId().getValue(), balance.getId().getValue(),
                userId, amount, balance.getUpdatedAt().getValue());
    }

    public WithdrawalCreatedResponse currencyBalanceToWithdrawalCreatedResponse(CurrencyBalance balance,
                                                                                Long withdrawalAmount,
                                                                                DepositWithdrawal depositWithdrawal,
                                                                                String message) {
        return new WithdrawalCreatedResponse(depositWithdrawal.getUserId().getValue(),
                balance.getAmount().getValue().longValue(),
                withdrawalAmount, depositWithdrawal.getUpdatedAt().getValue(), message);
    }
}
