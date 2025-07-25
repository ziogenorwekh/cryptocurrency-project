package shop.shportfolio.portfolio.application.port.output.repository;

import shop.shportfolio.portfolio.domain.view.UserBalanceView;

import java.util.Optional;
import java.util.UUID;

public interface PortfolioUserBalanceViewRepositoryPort {

    Optional<UserBalanceView> findUserBalanceByUserId(UUID userId);
}
