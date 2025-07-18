package shop.shportfolio.portfolio.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.domain.entity.*;
import shop.shportfolio.portfolio.domain.valueobject.*;

import java.time.LocalDateTime;

public interface PortfolioDomainService {


    Portfolio createPortfolio(PortfolioId portfolioId,
                              UserId userId, TotalAssetValue totalAssetValue,
                              CreatedAt createdAt, UpdatedAt updatedAt);

    Balance createBalance(BalanceId balanceId, PortfolioId portfolioId, MarketId marketId, Quantity quantity,
                          ValuationPrice valuationPrice, ValuationAmount valuationAmount, UpdatedAt updatedAt);

    ProfitLoss createProfitLoss(ProfitLossId profitLossId,
                                PortfolioId portfolioId,
                                MarketId marketId,
                                RealizedProfitLoss realizedProfitLoss,
                                UnrealizedProfitLoss unrealizedProfitLoss,
                                LocalDateTime timestamp,
                                UpdatedAt updatedAt);

    DepositWithdrawal createDepositWithdrawal(TransactionId transactionId,
                                              UserId userId,
                                              Amount amount,
                                              TransactionType transactionType,
                                              TransactionTime transactionTime,
                                              TransactionStatus transactionStatus,
                                              RelatedWalletAddress relatedWalletAddress,
                                              CreatedAt createdAt,
                                              UpdatedAt updatedAt);

    AssetChangeLog createAssetChangeLog(ChangeLogId changeLogId,
                                        PortfolioId portfolioId,
                                        ChangeType changeType,
                                        MarketId marketId,
                                        Amount changeAmount,
                                        ChangeDate changeDate,
                                        Description description,
                                        CreatedAt createdAt,
                                        UpdatedAt updatedAt);

}
