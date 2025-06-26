package shop.shportfolio.trading.application.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.trading.application.dto.OrderBookAsksDto;
import shop.shportfolio.trading.application.dto.OrderBookBidsDto;
import shop.shportfolio.trading.application.dto.OrderBookDto;
import shop.shportfolio.trading.domain.entity.OrderBook;
import shop.shportfolio.trading.domain.entity.PriceLevel;
import shop.shportfolio.trading.domain.valueobject.MarketItemTick;
import shop.shportfolio.trading.domain.valueobject.OrderSide;
import shop.shportfolio.trading.domain.valueobject.TickPrice;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.NavigableMap;
import java.util.TreeMap;

@Component
public class TradingDtoMapper {

    public OrderBook orderBookDtoToOrderBook(OrderBookDto orderBookDto, BigDecimal marketItemTick) {

        NavigableMap<TickPrice, PriceLevel> buyPriceLevels = new TreeMap<>(Comparator.reverseOrder());
        NavigableMap<TickPrice, PriceLevel> sellPriceLevels = new TreeMap<>();

        MarketId marketId = new MarketId(orderBookDto.getMarket());
        MarketItemTick tick = new MarketItemTick(marketItemTick);

        // 매수 호가
        for (OrderBookBidsDto bidDto : orderBookDto.getBids()) {
            TickPrice tickPrice = new TickPrice(BigDecimal.valueOf(bidDto.getBidPrice()));
            Quantity quantity = new Quantity(BigDecimal.valueOf(bidDto.getBidSize()));

            PriceLevel priceLevel = buyPriceLevels.computeIfAbsent(tickPrice, k -> new PriceLevel(k));

            // 외부 오더북은 단순 잔량 정보만 있어서 Order 생성은 더미 데이터로 넣음
            priceLevel.getBuyOrders().add(
                    ExternalOrderFactory.createOrder(
                            marketId,
                            OrderSide.BUY,
                            tickPrice,
                            quantity,
                            tick
                    )
            );
        }

        // 매도 호가
        for (OrderBookAsksDto askDto : orderBookDto.getAsks()) {
            TickPrice tickPrice = new TickPrice(BigDecimal.valueOf(askDto.getAskPrice()));
            Quantity quantity = new Quantity(BigDecimal.valueOf(askDto.getAskSize()));

            PriceLevel priceLevel = sellPriceLevels.computeIfAbsent(tickPrice, k -> new PriceLevel(k));

            priceLevel.getSellOrders().add(
                    ExternalOrderFactory.createOrder(
                            marketId,
                            OrderSide.SELL,
                            tickPrice,
                            quantity,
                            tick
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
