package shop.shportfolio.user.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.user.domain.entity.TransactionHistory;
import shop.shportfolio.user.domain.valueobject.TransactionHistoryId;
import shop.shportfolio.user.domain.valueobject.TransactionTime;

import java.time.LocalDateTime;

public interface TrHistoryDomainService {

    TransactionHistory save(
                            UserId userId, OrderId orderId, MarketId marketId,
                            TransactionType transactionType, OrderPrice orderPrice,
                            Quantity quantity, TransactionTime transactionTime);
}
