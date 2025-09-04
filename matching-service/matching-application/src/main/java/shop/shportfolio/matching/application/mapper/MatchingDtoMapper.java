package shop.shportfolio.matching.application.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.matching.application.dto.orderbook.OrderBookAsksBithumbDto;
import shop.shportfolio.matching.application.dto.orderbook.OrderBookBidsBithumbDto;
import shop.shportfolio.matching.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.matching.domain.entity.MatchingOrderBook;
import shop.shportfolio.matching.domain.entity.MatchingPriceLevel;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.valueobject.*;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.NavigableMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;

@Component
public class MatchingDtoMapper {

    public MatchingOrderBook orderBookDtoToOrderBook(OrderBookBithumbDto orderBookBithumbDto) {

        NavigableMap<TickPrice, MatchingPriceLevel> buyPriceLevels = new ConcurrentSkipListMap<>(Comparator.reverseOrder());
        NavigableMap<TickPrice, MatchingPriceLevel> sellPriceLevels = new ConcurrentSkipListMap<>();

        MarketId marketId = new MarketId(orderBookBithumbDto.getMarket());
        BigDecimal marketItemTick = BigDecimal.valueOf(orderBookBithumbDto.getTickPrice());
        MarketItemTick tick = new MarketItemTick(marketItemTick);

        // 매수 호가
        for (OrderBookBidsBithumbDto bidDto : orderBookBithumbDto.getBids()) {
            TickPrice tickPrice = TickPrice.of(BigDecimal.valueOf(bidDto.getBidPrice()), marketItemTick);
            Quantity quantity = new Quantity(BigDecimal.valueOf(bidDto.getBidSize()));

            MatchingPriceLevel matchingPriceLevel = buyPriceLevels.computeIfAbsent(tickPrice, MatchingPriceLevel::new);
            matchingPriceLevel.addOrder(
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

            MatchingPriceLevel matchingPriceLevel = sellPriceLevels.computeIfAbsent(tickPrice, k -> new MatchingPriceLevel(k));
            matchingPriceLevel.addOrder(
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

        return MatchingOrderBook.builder()
                .marketId(marketId)
                .marketItemTick(tick)
                .buyPriceLevels(buyPriceLevels)
                .sellPriceLevels(sellPriceLevels)
                .build();
    }
}
