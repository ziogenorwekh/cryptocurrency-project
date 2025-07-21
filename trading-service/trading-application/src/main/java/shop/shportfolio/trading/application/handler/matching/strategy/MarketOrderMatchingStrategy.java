package shop.shportfolio.trading.application.handler.matching.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.handler.UserBalanceHandler;
import shop.shportfolio.trading.application.handler.matching.OrderMatchProcessor;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
import shop.shportfolio.trading.application.handler.matching.FeeRateResolver;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.entity.orderbook.PriceLevel;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.event.TradingRecordedEvent;
import shop.shportfolio.trading.domain.valueobject.OrderType;
import shop.shportfolio.trading.domain.valueobject.TickPrice;

import java.util.*;

@Slf4j
@Component
public class MarketOrderMatchingStrategy implements OrderMatchingStrategy<MarketOrder> {

    private final FeeRateResolver feeRateResolver;
    private final UserBalanceHandler userBalanceHandler;
    private final OrderMatchProcessor matchProcessor;
    private final TradingOrderRepositoryPort tradingOrderRepository;

    public MarketOrderMatchingStrategy(
            FeeRateResolver feeRateResolver,
            UserBalanceHandler userBalanceHandler,
            OrderMatchProcessor matchProcessor,
            TradingOrderRepositoryPort tradingOrderRepository
    ) {
        this.feeRateResolver = feeRateResolver;
        this.userBalanceHandler = userBalanceHandler;
        this.matchProcessor = matchProcessor;
        this.tradingOrderRepository = tradingOrderRepository;
    }

    @Override
    public boolean supports(Order order) {
        return OrderType.MARKET.equals(order.getOrderType());
    }

    @Override
    public List<TradingRecordedEvent> match(OrderBook orderBook, MarketOrder marketOrder) {
        List<TradingRecordedEvent> trades = new ArrayList<>();

        UserBalance userBalance = userBalanceHandler.findUserBalanceByUserId(marketOrder.getUserId());

        NavigableMap<TickPrice, PriceLevel> priceLevels = marketOrder.isBuyOrder()
                ? orderBook.getSellPriceLevels()
                : orderBook.getBuyPriceLevels();

        FeeRate feeRate = feeRateResolver.resolve(marketOrder.getUserId(), marketOrder.getOrderSide());

        Iterator<Map.Entry<TickPrice, PriceLevel>> iterator = priceLevels.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<TickPrice, PriceLevel> entry = iterator.next();
            TickPrice tickPrice = entry.getKey();
            PriceLevel priceLevel = entry.getValue();

            trades.addAll(matchProcessor.processMarketOrder(
                    marketOrder, priceLevel, feeRate, userBalance));

            if (priceLevel.isEmpty()) {
                iterator.remove();
            }

            if (marketOrder.isFilled()) {
                tradingOrderRepository.saveMarketOrder(marketOrder);
                break;
            }
        }

        if (marketOrder.isUnfilled()) {
            log.info("MarketOrder {} unfilled after matching, canceling.", marketOrder.getId().getValue());
            marketOrder.cancel();
            tradingOrderRepository.saveMarketOrder(marketOrder);
        }

        userBalanceHandler.saveUserBalance(userBalance);
        log.info("MarketOrder {} has been successfully processed.", marketOrder.getId().getValue());
        return trades;
    }

}
