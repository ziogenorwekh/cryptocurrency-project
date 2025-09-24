package shop.shportfolio.portfolio.application.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.application.command.track.AssetChangLogTrackQuery;
import shop.shportfolio.portfolio.application.command.track.CryptoAssetTrackQuery;
import shop.shportfolio.portfolio.application.command.track.CryptoBalanceTrackQuery;
import shop.shportfolio.portfolio.application.dto.TradeKafkaResponse;
import shop.shportfolio.portfolio.application.exception.InvalidRequestException;
import shop.shportfolio.portfolio.application.port.output.repository.AssetChangeLogRepositoryPort;
import shop.shportfolio.portfolio.domain.AssetChangeLogDomainService;
import shop.shportfolio.portfolio.domain.entity.AssetChangeLog;
import shop.shportfolio.portfolio.domain.entity.DepositWithdrawal;
import shop.shportfolio.portfolio.domain.entity.Portfolio;
import shop.shportfolio.portfolio.domain.valueobject.ChangeLogId;
import shop.shportfolio.portfolio.domain.valueobject.ChangeType;
import shop.shportfolio.portfolio.domain.valueobject.PortfolioId;

import java.math.BigDecimal;
import java.util.List;
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
                new ChangeLogId(UUID.randomUUID()), portfolioId, depositWithdrawal.getUserId(), ChangeType.DEPOSIT,
                new MarketId("KRW"), depositWithdrawal.getAmount(),
                CreatedAt.now(),depositWithdrawal.getTransactionStatus());
        return repositoryPort.save(assetChangeLog);
    }

    public AssetChangeLog saveWithdrawal(DepositWithdrawal depositWithdrawal, PortfolioId portfolioId) {
        AssetChangeLog assetChangeLog = assetChangeLogDomainService.createAssetChangeLog(
                new ChangeLogId(UUID.randomUUID()), portfolioId,
                depositWithdrawal.getUserId(),
                ChangeType.WITHDRAWAL,
                new MarketId("KRW"), depositWithdrawal.getAmount(),
                CreatedAt.now(),depositWithdrawal.getTransactionStatus()
        );
        return repositoryPort.save(assetChangeLog);
    }

    public AssetChangeLog saveTrade(TradeKafkaResponse response, Portfolio portfolio) {
        ChangeType changeType;
        if (response.getTransactionType() == TransactionType.TRADE_SELL) {
            changeType = ChangeType.TRADE_SELL;
        } else if (response.getTransactionType() == TransactionType.TRADE_BUY) {
            changeType = ChangeType.TRADE_BUY;
        } else {
            throw new InvalidRequestException("transaction type not supported");
        }
        AssetChangeLog assetChangeLog = assetChangeLogDomainService.createAssetChangeLog(
                new ChangeLogId(UUID.randomUUID()), portfolio.getId(), portfolio.getUserId(), changeType,
                new MarketId(response.getMarketId()),
                Money.of(BigDecimal.valueOf(response.getOrderPrice())), CreatedAt.now(), TransactionStatus.COMPLETED
        );
        return repositoryPort.save(assetChangeLog);
    }

    public List<AssetChangeLog> trackAssetChangLog(AssetChangLogTrackQuery assetChangLogTrackQuery) {
        return repositoryPort.findAssetChangeLogsByUserId(assetChangLogTrackQuery.getUserId());
    }

    public List<AssetChangeLog> trackDepositWithdrawalAssetChangLogs(AssetChangLogTrackQuery assetChangLogTrackQuery) {
        return repositoryPort.findDepositWithdrawalAssetChangeLogsByUserId(assetChangLogTrackQuery.getUserId());
    }

    public List<AssetChangeLog> trackCryptoAssetChangLogs(AssetChangLogTrackQuery assetChangLogTrackQuery) {
        return repositoryPort.findCryptoAssetChangeLogsByUserId(assetChangLogTrackQuery.getUserId());
    }

    public List<AssetChangeLog> trackCryptoAssetChangLogs(CryptoAssetTrackQuery query) {
        return repositoryPort.findCryptoAssetChangeLogsByUserIdAndMarketId(query.getTokenUserId(), query.getMarketId());
    }

}
