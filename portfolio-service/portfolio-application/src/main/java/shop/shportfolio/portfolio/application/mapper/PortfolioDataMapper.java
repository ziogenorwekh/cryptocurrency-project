package shop.shportfolio.portfolio.application.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.dto.payment.PaymentPayRequest;
import shop.shportfolio.portfolio.application.command.*;
import shop.shportfolio.portfolio.domain.entity.Balance;
import shop.shportfolio.portfolio.domain.entity.Portfolio;
import shop.shportfolio.portfolio.domain.view.UserBalanceView;

@Component
public class PortfolioDataMapper {


    public MarketBalanceTrackQueryResponse balanceToMarketBalanceTrackQueryResponse(Balance balance) {
        return new MarketBalanceTrackQueryResponse(balance.getId().getValue(),
                balance.getPortfolioId().getValue(),
                balance.getMarketId().getValue(), balance.getQuantity().getValue(),
                balance.getPurchasePrice().getValue(),
                balance.getUpdatedAt().getValue());
    }

    public UserBalanceTrackQueryResponse userBalanceToUserBalanceTrackQueryResponse(UserBalanceView balance) {
        return new UserBalanceTrackQueryResponse(balance.getUserId().getValue(),
                balance.getAssetCode(), balance.getMoney().getValue());
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
}
