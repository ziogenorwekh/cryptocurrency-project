package shop.shportfolio.trading.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.entity.trade.Trade;
import shop.shportfolio.trading.domain.event.TradeCreatedEvent;
import shop.shportfolio.trading.domain.valueobject.TradeId;

public interface TradeDomainService {

    TradeCreatedEvent createTrade(TradeId tradeId, MarketId marketId , UserId userId, OrderId orderId,
                                  OrderPrice orderPrice, Quantity quantity,
                                  TransactionType transactionType, FeeAmount feeAmount, FeeRate feeRate);

    void applyExecutedTrade(OrderBook orderBook, Trade trade);

}
