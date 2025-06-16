package shop.shportfolio.user.domain;

import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.user.domain.entity.TransactionHistory;
import shop.shportfolio.user.domain.valueobject.Amount;
import shop.shportfolio.user.domain.valueobject.TransactionHistoryId;
import shop.shportfolio.user.domain.valueobject.TransactionTime;
import shop.shportfolio.user.domain.valueobject.TransactionType;

import java.time.LocalDateTime;
import java.util.UUID;

public class TrHistoryDomainServiceImpl implements TrHistoryDomainService {

    @Override
    public TransactionHistory save(UserId userId, MarketId marketId, TransactionType transactionType, Amount amount,
                                   LocalDateTime transactionTome) {
        return TransactionHistory.createTransactionHistory(new TransactionHistoryId(UUID.randomUUID()), userId
                , marketId, transactionType, amount, new TransactionTime(transactionTome));
    }
}
