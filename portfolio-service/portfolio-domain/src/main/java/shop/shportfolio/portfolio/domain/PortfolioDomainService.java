package shop.shportfolio.portfolio.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.domain.entity.*;
import shop.shportfolio.portfolio.domain.valueobject.*;

public interface PortfolioDomainService {


    Portfolio createPortfolio(PortfolioId portfolioId,
                              UserId userId, CreatedAt createdAt, UpdatedAt updatedAt);

    CryptoBalance createCryptoBalance(BalanceId balanceId, PortfolioId portfolioId, MarketId marketId, Quantity quantity,
                                      PurchasePrice purchasePrice, UpdatedAt updatedAt);

    CurrencyBalance createCurrencyBalance(BalanceId balanceId, PortfolioId portfolioId, MarketId marketId,
                                          Money money, UpdatedAt updatedAt, UserId userId);



    void addPurchase(CryptoBalance balance, PurchasePrice purchasePrice,Quantity amount);

    void subtractQuantity(CryptoBalance balance, Quantity quantity);

    void updateMoney(CurrencyBalance currencyBalance, Money money);

    void addMoney(CurrencyBalance balance, Money money);

    void subtractMoney(CurrencyBalance balance, Money money);

    void completeWithdrawal(DepositWithdrawal depositWithdrawal);

    void failWithdrawal(DepositWithdrawal depositWithdrawal);
}
