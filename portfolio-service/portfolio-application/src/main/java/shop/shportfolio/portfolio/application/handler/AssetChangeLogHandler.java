package shop.shportfolio.portfolio.application.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.CreatedAt;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.portfolio.application.port.output.repository.AssetChangeLogRepositoryPort;
import shop.shportfolio.portfolio.domain.AssetChangeLogDomainService;
import shop.shportfolio.portfolio.domain.entity.AssetChangeLog;
import shop.shportfolio.portfolio.domain.entity.DepositWithdrawal;
import shop.shportfolio.portfolio.domain.valueobject.ChangeLogId;
import shop.shportfolio.portfolio.domain.valueobject.ChangeType;
import shop.shportfolio.portfolio.domain.valueobject.PortfolioId;

import java.util.UUID;

@Component
public class AssetChangeLogHandler {

    private final AssetChangeLogRepositoryPort repositoryPort;
    private final AssetChangeLogDomainService assetChangeLogDomainService;
    @Autowired
    public AssetChangeLogHandler(AssetChangeLogRepositoryPort repositoryPort,
                                 AssetChangeLogDomainService assetChangeLogDomainService) {
        this.repositoryPort = repositoryPort;
        this.assetChangeLogDomainService = assetChangeLogDomainService;
    }

    public AssetChangeLog saveDeposit(DepositWithdrawal depositWithdrawal, PortfolioId portfolioId) {
        AssetChangeLog assetChangeLog = assetChangeLogDomainService.createAssetChangeLog(
                new ChangeLogId(UUID.randomUUID()), portfolioId,depositWithdrawal.getUserId(), ChangeType.DEPOSIT,
                new MarketId("KRW"), depositWithdrawal.getAmount(),
                CreatedAt.now());
        return repositoryPort.save(assetChangeLog);
    }

    public AssetChangeLog saveWithdrawal(DepositWithdrawal depositWithdrawal, PortfolioId portfolioId) {
        AssetChangeLog assetChangeLog = assetChangeLogDomainService.createAssetChangeLog(
                new ChangeLogId(UUID.randomUUID()),portfolioId,depositWithdrawal.getUserId(),ChangeType.WITHDRAWAL,
                new MarketId("KRW"), depositWithdrawal.getAmount(),
                CreatedAt.now()
        );
        return repositoryPort.save(assetChangeLog);
    }

}
