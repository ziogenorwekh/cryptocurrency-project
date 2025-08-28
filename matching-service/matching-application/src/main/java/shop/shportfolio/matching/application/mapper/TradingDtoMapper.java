package shop.shportfolio.matching.application.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.matching.application.dto.orderbook.OrderBookAsksBithumbDto;
import shop.shportfolio.matching.application.dto.orderbook.OrderBookBidsBithumbDto;
import shop.shportfolio.matching.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.entity.orderbook.PriceLevel;
import shop.shportfolio.trading.domain.valueobject.*;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.NavigableMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;

@Component
public class TradingDtoMapper {

    public OrderBook orderBookDtoToOrderBook(OrderBookBithumbDto orderBookBithumbDto, BigDecimal marketItemTick) {

        NavigableMap<TickPrice, PriceLevel> buyPriceLevels = new ConcurrentSkipListMap<>(Comparator.reverseOrder());
        NavigableMap<TickPrice, PriceLevel> sellPriceLevels = new ConcurrentSkipListMap<>();

        MarketId marketId = new MarketId(orderBookBithumbDto.getMarket());
        MarketItemTick tick = new MarketItemTick(marketItemTick);

        // 매수 호가
        for (OrderBookBidsBithumbDto bidDto : orderBookBithumbDto.getBids()) {
            TickPrice tickPrice = TickPrice.of(BigDecimal.valueOf(bidDto.getBidPrice()), marketItemTick);
            Quantity quantity = new Quantity(BigDecimal.valueOf(bidDto.getBidSize()));

            PriceLevel priceLevel = buyPriceLevels.computeIfAbsent(tickPrice, PriceLevel::new);
            priceLevel.addOrder(
                    new LimitOrder(
                            OrderId.anonymous(),
                            new UserId(UUID.randomUUID()),
                            new MarketId(orderBookBithumbDto.getMarket()),
                            OrderSide.BUY,
                            quantity,
                            quantity,
                            new OrderPrice(BigDecimal.valueOf(bidDto.getBidPrice())),
                            OrderType.LIMIT,
                            CreatedAt.now(),
                            OrderStatus.OPEN
                    )
            );
        }

        // 매도 호가
        for (OrderBookAsksBithumbDto askDto : orderBookBithumbDto.getAsks()) {
            Quantity quantity = new Quantity(BigDecimal.valueOf(askDto.getAskSize()));
            TickPrice tickPrice = TickPrice.of(BigDecimal.valueOf(askDto.getAskPrice()), marketItemTick);

            PriceLevel priceLevel = sellPriceLevels.computeIfAbsent(tickPrice, k -> new PriceLevel(k));
            priceLevel.addOrder(
                    new LimitOrder(
                            OrderId.anonymous(),
                            new UserId(UUID.randomUUID()),
                            new MarketId(orderBookBithumbDto.getMarket()),
                            OrderSide.SELL,
                            quantity,
                            quantity,
                            new OrderPrice(BigDecimal.valueOf(askDto.getAskPrice())),
                            OrderType.LIMIT,
                            CreatedAt.now(),
                            OrderStatus.OPEN
                    )
            );
        }

        return OrderBook.builder()
                .marketId(marketId)
                .marketItemTick(tick)
                .buyPriceLevels(buyPriceLevels)
                .sellPriceLevels(sellPriceLevels)
                .build();
    }
}
