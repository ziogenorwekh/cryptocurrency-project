package shop.shportfolio.portfolio.application.port.output.repository;

import shop.shportfolio.portfolio.domain.entity.Balance;
import shop.shportfolio.portfolio.domain.view.UserBalanceView;

import java.util.Optional;
import java.util.UUID;

public interface PortfolioRepositoryPort {

    Optional<Balance> findBalanceByPortfolioIdAndMarketId(UUID portfolioId, String marketId);

}
