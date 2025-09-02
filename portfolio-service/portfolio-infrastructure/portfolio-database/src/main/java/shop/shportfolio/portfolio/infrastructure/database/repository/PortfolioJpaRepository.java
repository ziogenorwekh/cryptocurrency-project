package shop.shportfolio.portfolio.infrastructure.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.portfolio.infrastructure.database.entity.PortfolioEntity;

import java.util.Optional;
import java.util.UUID;

public interface PortfolioJpaRepository extends JpaRepository<PortfolioEntity, UUID> {

    Optional<PortfolioEntity> findPortfolioEntityByPortfolioId(UUID portfolioId);

    Optional<PortfolioEntity> findPortfolioEntityByUserId(UUID userId);

    void deletePortfolioEntityByUserId(UUID userId);
}
