package shop.shportfolio.user.domain;

import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.user.domain.entity.TransactionHistory;
import shop.shportfolio.user.domain.valueobject.Amount;
import shop.shportfolio.user.domain.valueobject.TransactionType;

import java.time.LocalDateTime;

public interface TrHistoryDomainService {

    TransactionHistory save(UserId userId, MarketId marketId, TransactionType transactionType,
                            Amount amount, LocalDateTime transactionTome);
}
