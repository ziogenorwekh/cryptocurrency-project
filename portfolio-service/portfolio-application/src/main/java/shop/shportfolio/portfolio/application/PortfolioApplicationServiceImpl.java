package shop.shportfolio.portfolio.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import shop.shportfolio.common.domain.dto.payment.PaymentPayRequest;
import shop.shportfolio.common.domain.dto.payment.PaymentResponse;
import shop.shportfolio.common.domain.valueobject.PaymentStatus;
import shop.shportfolio.portfolio.application.command.*;
import shop.shportfolio.portfolio.application.handler.PortfolioPaymentHandler;
import shop.shportfolio.portfolio.application.handler.PortfolioCreateHandler;
import shop.shportfolio.portfolio.application.handler.PortfolioTrackHandler;
import shop.shportfolio.portfolio.application.mapper.PortfolioDataMapper;
import shop.shportfolio.portfolio.application.port.input.PortfolioApplicationService;
import shop.shportfolio.portfolio.domain.entity.Balance;
import shop.shportfolio.portfolio.domain.entity.Portfolio;
import shop.shportfolio.portfolio.domain.view.UserBalanceView;

@Slf4j
@Service
@Validated
public class PortfolioApplicationServiceImpl implements PortfolioApplicationService {

    private final PortfolioTrackHandler portfolioTrackHandler;
    private final PortfolioDataMapper portfolioDataMapper;
    private final PortfolioCreateHandler portfolioCreateHandler;
    private final PortfolioPaymentHandler portfolioPaymentHandler;

    @Autowired
    public PortfolioApplicationServiceImpl(PortfolioTrackHandler portfolioTrackHandler,
                                           PortfolioDataMapper portfolioDataMapper,
                                           PortfolioCreateHandler portfolioCreateHandler, PortfolioPaymentHandler portfolioPaymentHandler) {
        this.portfolioTrackHandler = portfolioTrackHandler;
        this.portfolioDataMapper = portfolioDataMapper;
        this.portfolioCreateHandler = portfolioCreateHandler;
        this.portfolioPaymentHandler = portfolioPaymentHandler;
    }


    @Override
    public MarketBalanceTrackQueryResponse trackMarketBalance(MarketBalanceTrackQuery marketBalanceTrackQuery) {
        Balance balance = portfolioTrackHandler.findBalanceByPortfolioIdAndMarketId(marketBalanceTrackQuery);
        return portfolioDataMapper.balanceToMarketBalanceTrackQueryResponse(balance);
    }

    @Override
    public UserBalanceTrackQueryResponse trackUserBalance(UserBalanceTrackQuery userBalanceTrackQuery) {
        UserBalanceView balance = portfolioTrackHandler.findUserBalanceByUserId(userBalanceTrackQuery);
        return portfolioDataMapper.userBalanceToUserBalanceTrackQueryResponse(balance);
    }

    @Override
    public TotalAssetValueTrackQueryResponse trackTotalAssetValue(TotalAssetValueTrackQuery totalAssetValueTrackQuery) {
        Portfolio portfolio = portfolioTrackHandler.findPortfolioByPortfolioIdAndUserId(totalAssetValueTrackQuery);
        return portfolioDataMapper.PortfolioToTotalAssetValueTrackQueryResponse(portfolio);
    }

    @Override
    public DepositCreatedResponse deposit(DepositCreateCommand depositCreateCommand) {
        PaymentPayRequest request = portfolioDataMapper.depositCreateCommandToPaymentPayRequest(depositCreateCommand);
        PaymentResponse paymentResponse = portfolioPaymentHandler.pay(request);
        if (paymentResponse.getStatus().equals(PaymentStatus.DONE)) {
            Portfolio portfolio = portfolioCreateHandler.deposit(depositCreateCommand, paymentResponse);

        }
        return null;
    }

    @Override
    public PortfolioCreatedResponse createPortfolio(PortfolioCreateCommand portfolioCreateCommand) {
        Portfolio portfolio = portfolioCreateHandler.createPortfolio(portfolioCreateCommand);
        return portfolioDataMapper.portfolioToPortfolioCreatedResponse(portfolio);
    }
}
