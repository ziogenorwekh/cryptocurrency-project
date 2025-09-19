package shop.shportfolio.portfolio.infrastructure.database.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import shop.shportfolio.portfolio.application.port.output.repository.AssetChangeLogRepositoryPort;
import shop.shportfolio.portfolio.domain.entity.AssetChangeLog;
import shop.shportfolio.portfolio.domain.valueobject.ChangeType;
import shop.shportfolio.portfolio.infrastructure.database.entity.AssetChangeLogEntity;
import shop.shportfolio.portfolio.infrastructure.database.mapper.PortfolioDataAccessMapper;
import shop.shportfolio.portfolio.infrastructure.database.repository.AssetChangeLogJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class AssetChangeLogRepositoryAdapter implements AssetChangeLogRepositoryPort {

    private final AssetChangeLogJpaRepository assetChangeLogJpaRepository;
    private final PortfolioDataAccessMapper portfolioDataAccessMapper;
    @Autowired
    public AssetChangeLogRepositoryAdapter(AssetChangeLogJpaRepository assetChangeLogJpaRepository,
                                           PortfolioDataAccessMapper portfolioDataAccessMapper) {
        this.assetChangeLogJpaRepository = assetChangeLogJpaRepository;
        this.portfolioDataAccessMapper = portfolioDataAccessMapper;
    }

    @Override
    public AssetChangeLog save(AssetChangeLog assetChangeLog) {
        AssetChangeLogEntity entity = portfolioDataAccessMapper.assetChangeLogToAssetChangeLogEntity(assetChangeLog);
        return portfolioDataAccessMapper.assetChangeLogEntityToAssetChangeLog(assetChangeLogJpaRepository.save(entity));
    }

    @Override
    public Optional<AssetChangeLog> findByPortfolioId(UUID portfolioId) {

        return assetChangeLogJpaRepository.findAssetChangeLogEntityByPortfolioId(portfolioId)
                .map(portfolioDataAccessMapper::assetChangeLogEntityToAssetChangeLog);
    }

    @Override
    public List<AssetChangeLog> findAssetChangeLogsByUserId(UUID userId) {
        return assetChangeLogJpaRepository.findAssetChangeLogEntitiesByUserId(userId)
                .stream().map(portfolioDataAccessMapper::assetChangeLogEntityToAssetChangeLog)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssetChangeLog> findDepositWithdrawalAssetChangeLogsByUserId(UUID userId) {
        List<AssetChangeLogEntity> entities =
                assetChangeLogJpaRepository.findAssetChangeLogEntitiesByUserIdAndChangeTypeInOrderByCreatedAtDesc(userId,
                        List.of(ChangeType.DEPOSIT, ChangeType.WITHDRAWAL));
        return entities.stream()
                .map(portfolioDataAccessMapper::assetChangeLogEntityToAssetChangeLog)
                .toList();
    }

    @Override
    public List<AssetChangeLog> findCryptoAssetChangeLogsByUserId(UUID userId) {
        List<AssetChangeLogEntity> entities =
                assetChangeLogJpaRepository.findAssetChangeLogEntitiesByUserIdAndChangeTypeInOrderByCreatedAtDesc(userId,
                        List.of(ChangeType.TRADE_BUY, ChangeType.TRADE_SELL));
        return entities.stream()
                .map(portfolioDataAccessMapper::assetChangeLogEntityToAssetChangeLog)
                .toList();
    }

    @Override
    public List<AssetChangeLog> findCryptoAssetChangeLogsByUserIdAndMarketId(UUID userId, String marketId) {
        return assetChangeLogJpaRepository.findAssetChangeLogEntitiesByUserIdAndMarketIdOrderByCreatedAtDesc(userId,marketId)
                .stream().map(portfolioDataAccessMapper::assetChangeLogEntityToAssetChangeLog).toList();
    }
}
