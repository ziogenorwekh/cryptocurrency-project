package shop.shportfolio.trading.application.handler.matching;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.FeeAmount;
import shop.shportfolio.common.domain.valueobject.FeeRate;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.TransactionType;
import shop.shportfolio.trading.application.handler.UserBalanceHandler;
import shop.shportfolio.trading.application.ports.output.repository.TradingTradeRecordRepositoryPort;
import shop.shportfolio.trading.domain.OrderDomainService;
import shop.shportfolio.trading.domain.TradeDomainService;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.entity.orderbook.PriceLevel;
import shop.shportfolio.trading.domain.entity.trade.Trade;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.event.TradingRecordedEvent;
import shop.shportfolio.trading.domain.valueobject.TickPrice;
import shop.shportfolio.trading.domain.valueobject.TradeId;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class OrderMatchProcessor {

    private final OrderDomainService orderDomainService;
    private final TradeDomainService tradeDomainService;
    private final TradingTradeRecordRepositoryPort tradeRepository;
    private final UserBalanceHandler userBalanceHandler;

    @Autowired
    public OrderMatchProcessor(OrderDomainService orderDomainService,
                               TradeDomainService tradeDomainService,
                               TradingTradeRecordRepositoryPort tradeRepository,
                               UserBalanceHandler userBalanceHandler) {
        this.orderDomainService = orderDomainService;
        this.tradeDomainService = tradeDomainService;
        this.tradeRepository = tradeRepository;
        this.userBalanceHandler = userBalanceHandler;
    }

    public List<TradingRecordedEvent> processReservation(
            OrderBook orderBook,
            ReservationOrder reservationOrder,
            FeeRate feeRate,
            UserBalance userBalance,
            OrderExecutionChecker executionChecker
    ) {
        List<TradingRecordedEvent> trades = new ArrayList<>();

        var counterPriceLevels = reservationOrder.isBuyOrder()
                ? orderBook.getSellPriceLevels()
                : orderBook.getBuyPriceLevels();

        for (Map.Entry<TickPrice, PriceLevel> entry : counterPriceLevels.entrySet()) {
            TickPrice tickPrice = entry.getKey();
            PriceLevel priceLevel = entry.getValue();

            OrderPrice executionPrice = new OrderPrice(tickPrice.getValue());

            if (!orderDomainService.isPriceMatch(reservationOrder, executionPrice)) {
                continue;
            }

            while (reservationOrder.isUnfilled() && !priceLevel.isEmpty()) {
                Order restingOrder = priceLevel.peekOrder();

                if (!executionChecker.isExecutable(reservationOrder, restingOrder.getOrderPrice())) {
                    log.info("[{}] Reservation execution condition not met. Stopping.", reservationOrder.getId().getValue());
                    break;
                }

                if (executionChecker.isExpired(reservationOrder)) {
                    log.info("[{}] Reservation expired during matching.", reservationOrder.getId().getValue());
                    break;
                }

                Quantity execQty = orderDomainService.applyOrder(reservationOrder, restingOrder.getRemainingQuantity());
                orderDomainService.applyOrder(restingOrder, execQty);

                FeeAmount feeAmount = feeRate.calculateFeeAmount(executionPrice, execQty);

                TradingRecordedEvent tradeEvent = tradeDomainService.createTrade(
                        new TradeId(UUID.randomUUID()),
                        reservationOrder.getMarketId(),
                        reservationOrder.getUserId(),
                        reservationOrder.getId(),
                        executionPrice,
                        execQty,
                        reservationOrder.isBuyOrder() ? TransactionType.TRADE_BUY : TransactionType.TRADE_SELL,
                        feeAmount,
                        feeRate
                );

                tradeRepository.saveTrade(tradeEvent.getDomainType());

                BigDecimal totalAmount = tradeEvent.getDomainType().getOrderPrice().getValue()
                        .multiply(tradeEvent.getDomainType().getQuantity().getValue())
                        .add(tradeEvent.getDomainType().getFeeAmount().getValue());

                userBalanceHandler.deduct(userBalance, reservationOrder.getId(), totalAmount);

                trades.add(tradeEvent);

                log.info("[{}] Executed trade: qty={}, price={}",
                        reservationOrder.getId().getValue(), execQty.getValue(), executionPrice.getValue());

                if (restingOrder.isFilled()) {
                    priceLevel.popOrder();
                }

                if (reservationOrder.isFilled()) {
                    break;
                }
            }

            if (reservationOrder.isFilled()) {
                break;
            }
        }

        return trades;
    }

    public List<TradingRecordedEvent> processLimitOrder(
            OrderBook orderBook,
            LimitOrder limitOrder,
            FeeRate feeRate,
            UserBalance userBalance,
            OrderExecutionChecker executionChecker
    ) {
        List<TradingRecordedEvent> trades = new ArrayList<>();

        var counterPriceLevels = limitOrder.isBuyOrder()
                ? orderBook.getSellPriceLevels()
                : orderBook.getBuyPriceLevels();

        for (Map.Entry<TickPrice, PriceLevel> entry : counterPriceLevels.entrySet()) {
            TickPrice tickPrice = entry.getKey();
            PriceLevel priceLevel = entry.getValue();

            if (!executionChecker.canMatchPrice(limitOrder, tickPrice)) {
                break;
            }

            OrderPrice executionPrice = new OrderPrice(tickPrice.getValue());

            while (limitOrder.isUnfilled() && !priceLevel.isEmpty()) {
                Order restingOrder = priceLevel.peekOrder();

                Quantity execQty = orderDomainService.applyOrder(limitOrder, restingOrder.getRemainingQuantity());
                orderDomainService.applyOrder(restingOrder, execQty);

                FeeAmount feeAmount = feeRate.calculateFeeAmount(executionPrice, execQty);

                TradingRecordedEvent tradeEvent = tradeDomainService.createTrade(
                        new TradeId(UUID.randomUUID()),
                        limitOrder.getMarketId(),
                        limitOrder.getUserId(),
                        limitOrder.getId(),
                        executionPrice,
                        execQty,
                        limitOrder.isBuyOrder() ? TransactionType.TRADE_BUY : TransactionType.TRADE_SELL,
                        feeAmount,
                        feeRate
                );

                tradeRepository.saveTrade(tradeEvent.getDomainType());

                BigDecimal totalAmount = tradeEvent.getDomainType().getOrderPrice().getValue()
                        .multiply(tradeEvent.getDomainType().getQuantity().getValue())
                        .add(tradeEvent.getDomainType().getFeeAmount().getValue());

                userBalanceHandler.deduct(userBalance, limitOrder.getId(), totalAmount);

                trades.add(tradeEvent);

                log.info("[{}] Executed trade: qty={}, price={}", limitOrder.getId().getValue(),
                        execQty.getValue(), executionPrice.getValue());

                if (restingOrder.isFilled()) {
                    priceLevel.popOrder();
                }
            }

            if (limitOrder.isFilled()) {
                break;
            }
        }

        return trades;
    }

    public List<TradingRecordedEvent> processMarketOrder(
            MarketOrder marketOrder,
            PriceLevel priceLevel,
            FeeRate feeRate,
            UserBalance userBalance) {

        List<TradingRecordedEvent> trades = new ArrayList<>();

        while (marketOrder.isUnfilled() && !priceLevel.isEmpty()) {
            Order restingOrder = priceLevel.peekOrder();

            Quantity restingRemainingQty = restingOrder.getRemainingQuantity();

            // marketOrder의 남은 가격으로 살 수 있는 최대 수량 계산
            BigDecimal maxQtyByPrice = marketOrder.getRemainingPrice().getValue()
                    .divide(restingOrder.getOrderPrice().getValue(), 8, BigDecimal.ROUND_DOWN);

            Quantity execQty = Quantity.of(maxQtyByPrice.min(restingRemainingQty.getValue()));

            if (execQty.isZero()) {
                break;
            }

            // restingOrder와 marketOrder에 체결 수량 적용
            orderDomainService.applyOrder(restingOrder, execQty);
            orderDomainService.applyMarketOrder(marketOrder, execQty, restingOrder.getOrderPrice());
            // marketOrder 잔여가격 차감 (execQty * restingOrder 가격)
            // 잔여가격 setter가 없으므로 리플렉션/변경하거나 생성자 변경 필요
            // 가정: MarketOrder에 setRemainingPrice(OrderPrice) 메서드 존재

            OrderPrice executionPrice = restingOrder.getOrderPrice();
            FeeAmount feeAmount = feeRate.calculateFeeAmount(executionPrice, execQty);

            TradingRecordedEvent tradeEvent = tradeDomainService.createTrade(
                    new TradeId(UUID.randomUUID()),
                    marketOrder.getMarketId(),
                    marketOrder.getUserId(),
                    marketOrder.getId(),
                    executionPrice,
                    execQty,
                    marketOrder.isBuyOrder() ? TransactionType.TRADE_BUY : TransactionType.TRADE_SELL,
                    feeAmount,
                    feeRate
            );

            tradeRepository.saveTrade(tradeEvent.getDomainType());

            BigDecimal totalAmount = tradeEvent.getDomainType().getOrderPrice().getValue()
                    .multiply(tradeEvent.getDomainType().getQuantity().getValue())
                    .add(tradeEvent.getDomainType().getFeeAmount().getValue());

            userBalanceHandler.deduct(userBalance, marketOrder.getId(), totalAmount);

            trades.add(tradeEvent);

            log.info("[MarketOrder] Executed trade: qty={}, price={}",
                    execQty.getValue(), executionPrice.getValue());

            log.info("[OrderBook] restingOrder.isFilled() = {}", restingOrder.isFilled());
            if (restingOrder.isFilled()) {
                priceLevel.popOrder();
                log.info("[OrderBook] PriceLevel after popOrder: remaining orders count={}",
                        priceLevel.getOrders().size());
            }

            if (marketOrder.getRemainingPrice().isZero()) {
                break;
            }

        }

        return trades;
    }
}
