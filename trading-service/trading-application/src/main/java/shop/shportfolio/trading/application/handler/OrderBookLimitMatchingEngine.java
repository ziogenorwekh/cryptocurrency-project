package shop.shportfolio.trading.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.CreatedAt;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.TransactionType;
import shop.shportfolio.trading.application.ports.output.repository.TradingRepositoryAdapter;
import shop.shportfolio.trading.domain.TradingDomainService;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.event.TradingRecordedEvent;
import shop.shportfolio.trading.domain.valueobject.TickPrice;
import shop.shportfolio.trading.domain.valueobject.TradeId;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiFunction;

@Slf4j
@Component
public class OrderBookLimitMatchingEngine {

    private final TradingDomainService tradingDomainService;
    private final TradingRepositoryAdapter tradingRepository;

    @Autowired
    public OrderBookLimitMatchingEngine(TradingDomainService tradingDomainService, TradingRepositoryAdapter tradingRepository) {
        this.tradingDomainService = tradingDomainService;
        this.tradingRepository = tradingRepository;
    }


    public List<TradingRecordedEvent> execBidLimitOrder(OrderBook orderBook, LimitOrder limitOrder) {
        return execLimitOrder(
                limitOrder,
                orderBook, orderBook.getSellPriceLevels(),
                (tradeId, qty) -> tradingDomainService.createTrade(
                        tradeId, limitOrder.getUserId(), limitOrder.getId(),
                        limitOrder.getOrderPrice(), qty, new CreatedAt(LocalDateTime.now()),
                        TransactionType.TRADE_SELL));
    }

    public List<TradingRecordedEvent> execAsksLimitOrder(OrderBook orderBook, LimitOrder limitOrder) {
        return execLimitOrder(
                limitOrder, orderBook, orderBook.getBuyPriceLevels(),
                (tradeId, qty) -> tradingDomainService.createTrade(
                        tradeId, limitOrder.getUserId(), limitOrder.getId(),
                        limitOrder.getOrderPrice(), qty, new CreatedAt(LocalDateTime.now()),
                        TransactionType.TRADE_BUY));
    }

    /**
     * 리밋 오더 매칭 핵심 메서드
     */
    private List<TradingRecordedEvent> execLimitOrder(LimitOrder limitOrder,
                                                      OrderBook orderBook,
                                                      NavigableMap<TickPrice, PriceLevel> counterPriceLevels,
                                                      BiFunction<TradeId, Quantity, TradingRecordedEvent> tradeEventCreator) {
        List<TradingRecordedEvent> trades = new ArrayList<>();

        log.info("Start executing LimitOrder. OrderId={}, RemainingQty={}",
                limitOrder.getId().getValue(), limitOrder.getRemainingQuantity().getValue());

        // 가격 우선, 시간 우선으로 매칭
        Iterator<Map.Entry<TickPrice, PriceLevel>> iterator = counterPriceLevels.entrySet().iterator();

        while (limitOrder.isOpen() && iterator.hasNext()) {
            Map.Entry<TickPrice, PriceLevel> entry = iterator.next();
            TickPrice price = entry.getKey();
            PriceLevel priceLevel = entry.getValue();

            // 가격 조건 체크
            if (!tradingDomainService.canMatchPrice(limitOrder, price)) {
                break;  // 더 이상 매칭 불가 가격
            }

            // 해당 가격 레벨에서 FIFO 매칭
            while (limitOrder.isOpen() && !priceLevel.isEmpty()) {
                Order restingOrder = priceLevel.peekOrder();

                Quantity execQty = tradingDomainService.applyOrder(limitOrder, restingOrder.getRemainingQuantity());
                tradingDomainService.applyOrder(restingOrder, execQty);

                TradingRecordedEvent tradeEvent = tradeEventCreator.apply(new TradeId(UUID.randomUUID()), execQty);
                tradingRepository.saveTrade(tradeEvent.getDomainType());
                trades.add(tradeEvent);

                log.info("Executed trade: {} qty at price {}", execQty.getValue(), price.getValue());

                if (restingOrder.isFilled()) {
                    priceLevel.popOrder();
                }
            }

            // 가격 레벨이 비면 제거
            if (priceLevel.isEmpty()) {
                iterator.remove();
            }
        }

        // 잔량 → 내 호가창에 추가
        if (limitOrder.isOpen()) {
            TickPrice tickPrice = TickPrice.of(limitOrder.getOrderPrice().getValue(),
                    orderBook.getMarketItemTick().getValue());
            NavigableMap<TickPrice, PriceLevel> ownPriceLevels = limitOrder.getOrderSide().isBuy() ?
                    orderBook.getBuyPriceLevels() : orderBook.getSellPriceLevels();

            PriceLevel priceLevel = ownPriceLevels.computeIfAbsent(tickPrice, k -> new PriceLevel(tickPrice));
            priceLevel.addOrder(limitOrder);
            log.info("Limit order {} partially/unfilled → added to orderbook at price {}", limitOrder.getId().getValue(), tickPrice.getValue());
        }

        return trades;
    }
}
