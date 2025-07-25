package shop.shportfolio.portfolio.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import shop.shportfolio.portfolio.application.command.*;
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
    @Autowired
    public PortfolioApplicationServiceImpl(PortfolioTrackHandler portfolioTrackHandler,
                                           PortfolioDataMapper portfolioDataMapper) {
        this.portfolioTrackHandler = portfolioTrackHandler;
        this.portfolioDataMapper = portfolioDataMapper;
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
}
