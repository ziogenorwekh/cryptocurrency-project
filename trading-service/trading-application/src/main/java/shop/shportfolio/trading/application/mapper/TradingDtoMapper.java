package shop.shportfolio.trading.application.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.application.dto.marketdata.MarketItemBithumbDto;
import shop.shportfolio.trading.application.dto.marketdata.candle.CandleMinuteRequestDto;
import shop.shportfolio.trading.application.dto.marketdata.candle.CandleRequestDto;
import shop.shportfolio.trading.application.dto.marketdata.ticker.MarketTickerResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.trade.TradeTickRequestDto;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookAsksBithumbDto;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBidsBithumbDto;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.UUID;

@Component
public class TradingDtoMapper {

    public OrderBook orderBookDtoToOrderBook(OrderBookBithumbDto orderBookBithumbDto, BigDecimal marketItemTick) {

        NavigableMap<TickPrice, PriceLevel> buyPriceLevels = new TreeMap<>(Comparator.reverseOrder());
        NavigableMap<TickPrice, PriceLevel> sellPriceLevels = new TreeMap<>();

        MarketId marketId = new MarketId(orderBookBithumbDto.getMarket());
        MarketItemTick tick = new MarketItemTick(marketItemTick);

        // 매수 호가
        for (OrderBookBidsBithumbDto bidDto : orderBookBithumbDto.getBids()) {
            TickPrice tickPrice = TickPrice.of(BigDecimal.valueOf(bidDto.getBidPrice()), marketItemTick);
            Quantity quantity = new Quantity(BigDecimal.valueOf(bidDto.getBidSize()));

            PriceLevel priceLevel = buyPriceLevels.computeIfAbsent(tickPrice, k -> new PriceLevel(k));
            priceLevel.getOrders().add(
                    LimitOrder.createLimitOrder(
                            new UserId(UUID.randomUUID()),
                            new MarketId(orderBookBithumbDto.getMarket()),
                            OrderSide.BUY,
                            quantity,
                            new OrderPrice(BigDecimal.valueOf(bidDto.getBidPrice())),
                            OrderType.LIMIT
                            )
            );
        }

        // 매도 호가
        for (OrderBookAsksBithumbDto askDto : orderBookBithumbDto.getAsks()) {
            Quantity quantity = new Quantity(BigDecimal.valueOf(askDto.getAskSize()));
            TickPrice tickPrice = TickPrice.of(BigDecimal.valueOf(askDto.getAskPrice()), marketItemTick);

            PriceLevel priceLevel = sellPriceLevels.computeIfAbsent(tickPrice, k -> new PriceLevel(k));

            priceLevel.getOrders().add(
                    LimitOrder.createLimitOrder(
                            new UserId(UUID.randomUUID()),
                            new MarketId(orderBookBithumbDto.getMarket()),
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

    public MarketItem marketItemBithumbDtoToMarketItem(MarketItemBithumbDto marketItemBithumbDto,
                                                       Integer tickPrice) {
        return MarketItem.builder()
                .marketId(marketItemBithumbDto.getMarketId())
                .marketKoreanName(new MarketKoreanName(marketItemBithumbDto.getKoreanName()))
                .marketEnglishName(new MarketEnglishName(marketItemBithumbDto.getEnglishName()))
                .marketStatus(MarketStatus.ACTIVE)
                .tickPrice(new TickPrice(BigDecimal.valueOf(tickPrice)))
                .marketWarning(null)
                .build();
    }

    public CandleMinuteRequestDto toCandleRequestMinuteDto(Integer unit, String marketId, String to, Integer count) {
        return CandleMinuteRequestDto.builder()
                .market(marketId)
                .to(to)
                .count(count)
                .unit(unit)
                .build();
    }

    public CandleRequestDto toCandleRequestDto(String marketId, String to, Integer count) {
        return CandleRequestDto.builder()
                .market(marketId)
                .to(to)
                .count(count)
                .build();
    }

    public TradeTickRequestDto toTradeTickRequestDto(String marketId, String to, Integer count,String cursor,
                                                     Integer daysAgo) {
        return TradeTickRequestDto.builder()
                .market(marketId)
                .to(to)
                .count(count)
                .cursor(cursor == null ? "" : cursor)
                .daysAgo(daysAgo)
                .build();
    }

    public MarketTickerResponseDto tradeToMarketTickerResponseDto(Trade trade, MarketTickerResponseDto baseDto) {
        // baseDto는 외부 API에서 온 전체 데이터
        // trade의 시간, 가격, 거래량 등만 덮어쓰기

        LocalDateTime tradeCreatedAt = trade.getCreatedAt().getValue();
        long tradeTimestamp = tradeCreatedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        return MarketTickerResponseDto.builder()
                .market(baseDto.getMarket())
                .tradeDate(tradeCreatedAt.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .tradeTime(tradeCreatedAt.format(DateTimeFormatter.ofPattern("HHmmss")))
                .tradeDateKst(baseDto.getTradeDateKst())
                .tradeTimeKst(baseDto.getTradeTimeKst())
                .tradeTimestamp(tradeTimestamp)
                .openingPrice(baseDto.getOpeningPrice())
                .highPrice(baseDto.getHighPrice())
                .lowPrice(baseDto.getLowPrice())
                .tradePrice(trade.getOrderPrice().getValue().doubleValue())
                .prevClosingPrice(baseDto.getPrevClosingPrice())
                .change(baseDto.getChange())
                .changePrice(baseDto.getChangePrice())
                .changeRate(baseDto.getChangeRate())
                .signedChangePrice(baseDto.getSignedChangePrice())
                .signedChangeRate(baseDto.getSignedChangeRate())
                .tradeVolume(trade.getQuantity().getValue().doubleValue())
                .accTradePrice(baseDto.getAccTradePrice())
                .accTradePrice24h(baseDto.getAccTradePrice24h())
                .accTradeVolume(baseDto.getAccTradeVolume())
                .accTradeVolume24h(baseDto.getAccTradeVolume24h())
                .highest52WeekPrice(baseDto.getHighest52WeekPrice())
                .highest52WeekDate(baseDto.getHighest52WeekDate())
                .lowest52WeekPrice(baseDto.getLowest52WeekPrice())
                .lowest52WeekDate(baseDto.getLowest52WeekDate())
                .timestamp(baseDto.getTimestamp())
                .build();
    }

    // 변환 함수 추가
    private LocalDateTime convertTimestampToLocalDateTime(long timestamp) {
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
