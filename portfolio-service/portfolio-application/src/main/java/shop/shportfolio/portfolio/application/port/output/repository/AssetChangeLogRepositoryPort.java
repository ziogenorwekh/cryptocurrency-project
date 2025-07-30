package shop.shportfolio.portfolio.application.port.output.repository;

import shop.shportfolio.portfolio.domain.entity.AssetChangeLog;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetChangeLogRepositoryPort {

    AssetChangeLog save(AssetChangeLog assetChangeLog);
    Optional<AssetChangeLog> findByPortfolioId(UUID portfolioId);
    List<AssetChangeLog> findAssetChangeLogsByUserId(UUID userId);
}
