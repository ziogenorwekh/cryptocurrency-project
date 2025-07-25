package shop.shportfolio.portfolio.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.portfolio.application.command.MarketBalanceTrackQuery;
import shop.shportfolio.portfolio.application.command.TotalAssetValueTrackQuery;
import shop.shportfolio.portfolio.application.command.UserBalanceTrackQuery;
import shop.shportfolio.portfolio.application.exception.BalanceNotFoundException;
import shop.shportfolio.portfolio.application.exception.PortfolioNotFoundException;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioRepositoryPort;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioUserBalanceViewRepositoryPort;
import shop.shportfolio.portfolio.domain.entity.Balance;
import shop.shportfolio.portfolio.domain.entity.Portfolio;
import shop.shportfolio.portfolio.domain.view.UserBalanceView;

import java.util.UUID;

@Slf4j
@Component
public class PortfolioTrackHandler {


    private final PortfolioRepositoryPort portfolioRepository;
    private final PortfolioUserBalanceViewRepositoryPort portfolioUserBalanceViewRepositoryPort;
    @Autowired
    public PortfolioTrackHandler(PortfolioRepositoryPort portfolioRepository,
                                 PortfolioUserBalanceViewRepositoryPort portfolioUserBalanceViewRepositoryPort) {
        this.portfolioRepository = portfolioRepository;
        this.portfolioUserBalanceViewRepositoryPort = portfolioUserBalanceViewRepositoryPort;
    }

    public Balance findBalanceByPortfolioIdAndMarketId(MarketBalanceTrackQuery query) {
        return portfolioRepository.findBalanceByPortfolioIdAndMarketId(query.getPortfolioId(), query.getMarketId())
                .orElseThrow(() -> new BalanceNotFoundException(String.format("marketId: {} , userId: {} is not found. ",
                        query.getMarketId(), query.getPortfolioId())));
    }

    public UserBalanceView findUserBalanceByUserId(UserBalanceTrackQuery query) {
        return portfolioUserBalanceViewRepositoryPort.findUserBalanceByUserId(query.getUserId()).orElseThrow(
                ()-> new BalanceNotFoundException(String.format("UserBalanceView %s is not found.", query.getUserId()))
        );
    }

    public Portfolio findPortfolioByPortfolioIdAndUserId(TotalAssetValueTrackQuery query) {
        return portfolioRepository.findPortfolioByPortfolioIdAndUserId(query.getPortfolioId(), query.getUserId())
                .orElseThrow(()->new PortfolioNotFoundException(String.format("userId: {}, portfolioId: {} is not found.",
                        query.getUserId(),query.getPortfolioId())));
    }
}
