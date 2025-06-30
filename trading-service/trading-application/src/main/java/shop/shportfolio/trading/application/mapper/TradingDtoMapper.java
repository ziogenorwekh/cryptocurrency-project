package shop.shportfolio.trading.application.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.application.dto.OrderBookAsksDto;
import shop.shportfolio.trading.application.dto.OrderBookBidsDto;
import shop.shportfolio.trading.application.dto.OrderBookDto;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.OrderBook;
import shop.shportfolio.trading.domain.entity.PriceLevel;
import shop.shportfolio.trading.domain.valueobject.MarketItemTick;
import shop.shportfolio.trading.domain.valueobject.OrderSide;
import shop.shportfolio.trading.domain.valueobject.OrderType;
import shop.shportfolio.trading.domain.valueobject.TickPrice;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.UUID;

@Component
public class TradingDtoMapper {

    public OrderBook orderBookDtoToOrderBook(OrderBookDto orderBookDto, BigDecimal marketItemTick) {

        NavigableMap<TickPrice, PriceLevel> buyPriceLevels = new TreeMap<>(Comparator.reverseOrder());
        NavigableMap<TickPrice, PriceLevel> sellPriceLevels = new TreeMap<>();

        MarketId marketId = new MarketId(orderBookDto.getMarket());
        MarketItemTick tick = new MarketItemTick(marketItemTick);

        // 매수 호가
        for (OrderBookBidsDto bidDto : orderBookDto.getBids()) {
            TickPrice tickPrice = TickPrice.of(BigDecimal.valueOf(bidDto.getBidPrice()), marketItemTick);
            Quantity quantity = new Quantity(BigDecimal.valueOf(bidDto.getBidSize()));

            PriceLevel priceLevel = buyPriceLevels.computeIfAbsent(tickPrice, k -> new PriceLevel(k));
            priceLevel.getOrders().add(
                    LimitOrder.createLimitOrder(
                            new UserId(UUID.randomUUID()),
                            new MarketId(orderBookDto.getMarket()),
                            OrderSide.BUY,
                            quantity,
                            new OrderPrice(BigDecimal.valueOf(bidDto.getBidPrice())),
                            OrderType.LIMIT
                            )
            );
        }

        // 매도 호가
        for (OrderBookAsksDto askDto : orderBookDto.getAsks()) {
            Quantity quantity = new Quantity(BigDecimal.valueOf(askDto.getAskSize()));
            TickPrice tickPrice = TickPrice.of(BigDecimal.valueOf(askDto.getAskPrice()), marketItemTick);

            PriceLevel priceLevel = sellPriceLevels.computeIfAbsent(tickPrice, k -> new PriceLevel(k));

            priceLevel.getOrders().add(
                    LimitOrder.createLimitOrder(
                            new UserId(UUID.randomUUID()),
                            new MarketId(orderBookDto.getMarket()),
                            OrderSide.SELL,
                            quantity,
                            new OrderPrice(BigDecimal.valueOf(askDto.getAskPrice())),
                            OrderType.LIMIT
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


    // 변환 함수 추가
    private LocalDateTime convertTimestampToLocalDateTime(long timestamp) {
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
