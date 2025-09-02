package shop.shportfolio.portfolio.infrastructure.database.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.domain.entity.*;
import shop.shportfolio.portfolio.domain.valueobject.*;
import shop.shportfolio.portfolio.infrastructure.database.entity.*;

@Component
public class PortfolioDataAccessMapper {

    public AssetChangeLog assetChangeLogEntityToAssetChangeLog(AssetChangeLogEntity entity) {
        return new AssetChangeLog(
                new ChangeLogId(entity.getChangeLogId()),
                new PortfolioId(entity.getPortfolioId()),
                new UserId(entity.getUserId()),
                entity.getChangeType(),
                new MarketId(entity.getMarketId()),
                new Money(entity.getChangeMoney()),
                new CreatedAt(entity.getCreatedAt()),
                new Description(entity.getDescription())
        );
    }

    public AssetChangeLogEntity assetChangeLogToAssetChangeLogEntity(AssetChangeLog assetChangeLog) {
        return new AssetChangeLogEntity(
                assetChangeLog.getId().getValue(),
                assetChangeLog.getPortfolioId().getValue(),
                assetChangeLog.getUserId().getValue(),
                assetChangeLog.getMarketId().getValue(),
                assetChangeLog.getChangeMoney().getValue(),
                assetChangeLog.getChangeType(),
                assetChangeLog.getDescription().getValue(),
                assetChangeLog.getCreatedAt().getValue()
        );
    }

    public Payment paymentEntityToPayment(PaymentEntity entity) {
        return new Payment(
                new PaymentId(entity.getPaymentId()),
                new UserId(entity.getUserId()),
                new PaymentKey(entity.getPaymentKey()),
                new OrderPrice(entity.getTotalAmount()),
                entity.getPaymentMethod(),
                entity.getStatus(),
                new Description(entity.getDescription()),
                entity.getRawResponse(),
                new CancelReason(entity.getCancelReason()),
                new CancelledAt(entity.getCancelledAt())
        );
    }

    public PaymentEntity paymentToPaymentEntity(Payment payment) {
        return new PaymentEntity(
                payment.getId().getValue(),
                payment.getUserId().getValue(),
                payment.getPaymentKey().getValue(),
                payment.getTotalAmount().getValue(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getDescription().getValue(),
                payment.getRawResponse(),
                payment.getCancelReason().getValue(),
                payment.getCancelledAt().getValue()
        );
    }

    public CryptoBalance cryptoBalanceEntityToCryptoBalance(CryptoBalanceEntity entity) {
        return new CryptoBalance(
                new BalanceId(entity.getBalanceId()),
                new PortfolioId(entity.getPortfolioId()),
                new MarketId(entity.getMarketId()),
                new UpdatedAt(entity.getUpdatedAt()),
                new Quantity(entity.getQuantity()),
                new PurchasePrice(entity.getPurchasePrice()));
    }

    public CryptoBalanceEntity cryptoBalanceToCryptoBalanceEntity(CryptoBalance cryptoBalance) {
        return new CryptoBalanceEntity(
                cryptoBalance.getId().getValue(),
                cryptoBalance.getPortfolioId().getValue(),
                cryptoBalance.getMarketId().getValue(),
                cryptoBalance.getUpdatedAt().getValue(),
                cryptoBalance.getQuantity().getValue(),
                cryptoBalance.getPurchasePrice().getValue());
    }

    public CurrencyBalance currencyBalanceEntityToCurrencyBalance(CurrencyBalanceEntity entity) {
        return new CurrencyBalance(
                new BalanceId(entity.getBalanceId()),
                new PortfolioId(entity.getPortfolioId()),
                new MarketId(entity.getMarketId()),
                new UpdatedAt(entity.getUpdatedAt()),
                new Money(entity.getMoney()),
                new UserId(entity.getUserId()));
    }

    public CurrencyBalanceEntity currencyBalanceToCurrencyBalanceEntity(CurrencyBalance currencyBalance) {
        return new CurrencyBalanceEntity(
                currencyBalance.getId().getValue(),
                currencyBalance.getPortfolioId().getValue(),
                currencyBalance.getMarketId().getValue(),
                currencyBalance.getUpdatedAt().getValue(),
                currencyBalance.getUserId().getValue(),
                currencyBalance.getAmount().getValue());
    }

    public DepositWithdrawal depositWithdrawalEntityToDepositWithdrawal(DepositWithdrawalEntity entity) {
        return new DepositWithdrawal(
                new TransactionId(entity.getTransactionId()),
                new UserId(entity.getUserId()),
                new Money(entity.getAmount()),
                entity.getTransactionType(),
                new TransactionTime(entity.getTransactionTime()),
                entity.getTransactionStatus(),
                new RelatedWalletAddress(entity.getRelatedWalletAddress(), entity.getBankName(), entity.getWalletType()),
                new CreatedAt(entity.getCreatedAt()),
                new UpdatedAt(entity.getUpdatedAt()));
    }

    public DepositWithdrawalEntity depositWithdrawalToDepositWithdrawalEntity(DepositWithdrawal depositWithdrawal) {
        return new DepositWithdrawalEntity(
                depositWithdrawal.getId().getValue(),
                depositWithdrawal.getUserId().getValue(),
                depositWithdrawal.getAmount().getValue(),
                depositWithdrawal.getTransactionType(),
                depositWithdrawal.getTransactionTime().getValue(),
                depositWithdrawal.getTransactionStatus(),
                depositWithdrawal.getRelatedWalletAddress().getValue(),
                depositWithdrawal.getRelatedWalletAddress().getWalletType(),
                depositWithdrawal.getRelatedWalletAddress().getBankName(),
                depositWithdrawal.getCreatedAt().getValue(),
                depositWithdrawal.getUpdatedAt().getValue());
    }

    public Portfolio portfolioEntityToPortfolio(PortfolioEntity entity) {
        return new Portfolio(
                new PortfolioId(entity.getPortfolioId()),
                new UserId(entity.getUserId()),
                new CreatedAt(entity.getCreatedAt()),
                new UpdatedAt(entity.getUpdatedAt())
        );
    }

    public PortfolioEntity portfolioToPortfolioEntity(Portfolio portfolio) {
        return new PortfolioEntity(
                portfolio.getId().getValue(),
                portfolio.getUserId().getValue(),
                portfolio.getCreatedAt().getValue(),
                portfolio.getUpdatedAt().getValue()
        );
    }
}
