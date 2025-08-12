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
import shop.shportfolio.trading.application.dto.marketdata.trade.TradeTickResponseDto;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookAsksBithumbDto;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBidsBithumbDto;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.trading.application.support.UUIDSupport;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.entity.orderbook.MarketItem;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.entity.orderbook.PriceLevel;
import shop.shportfolio.trading.domain.entity.trade.Trade;
import shop.shportfolio.trading.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.*;
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
                .unit(unit)
                .market(marketId)
                .to(to == null ? "" : to)
                .count(count == null ? 0 : count)
                .build();
    }

    public CandleRequestDto toCandleRequestDto(String marketId, String to, Integer count) {
        return CandleRequestDto.builder()
                .market(marketId)
                .to(to == null ? "" : to)
                .count(count == null ? 1 : count)
                .build();
    }

    public TradeTickRequestDto toTradeTickRequestDto(String marketId, String to, Integer count,String cursor,
                                                     Integer daysAgo) {
        return TradeTickRequestDto.builder()
                .market(marketId)
                .to(to ==  null ? "" : to)
                .count(count == null ? 1 : count)
                .cursor(cursor == null ? "" : cursor)
                .daysAgo(daysAgo == null ? 0 : daysAgo)
                .build();
    }

    public MarketTickerResponseDto tradeToMarketTickerResponseDto(Trade trade, MarketTickerResponseDto baseDto) {
        // baseDto는 외부 API에서 온 전체 데이터
        // trade의 시간, 가격, 거래량 등만 덮어쓰기

        LocalDateTime tradeCreatedAt = trade.getCreatedAt().getValue();
        ZonedDateTime tradeCreatedAtUtc = tradeCreatedAt.atZone(ZoneOffset.UTC);
        long tradeTimestamp = tradeCreatedAtUtc.toInstant().toEpochMilli();
        ZonedDateTime tradeCreatedAtKst = tradeCreatedAtUtc.withZoneSameInstant(ZoneId.of("Asia/Seoul"));
        long tradeTimestampKst = tradeCreatedAtKst.toInstant().toEpochMilli();
        return MarketTickerResponseDto.builder()
                .market(baseDto.getMarket())
                .tradeDate(tradeCreatedAt.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .tradeTime(tradeCreatedAt.format(DateTimeFormatter.ofPattern("HHmmss")))
                .tradeDateKst(tradeCreatedAtKst.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .tradeTimeKst(tradeCreatedAtKst.format(DateTimeFormatter.ofPattern("HHmmss")))
                .tradeTimestamp(tradeTimestampKst)
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
                .timestamp(tradeTimestamp)
                .build();
    }

    public TradeTickResponseDto tradeToTradeTickResponseDto(Trade trade) {
        LocalDateTime createdAt = trade.getCreatedAt().getValue();
        ZonedDateTime createdAtUtc = createdAt.atZone(ZoneOffset.UTC);
        long timestamp = createdAtUtc.toInstant().toEpochMilli();

        return TradeTickResponseDto.builder()
                .market(trade.getMarketId().getValue())
                .tradeDateUtc(createdAtUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .tradeTimeUtc(createdAtUtc.format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                .timestamp(timestamp)
                .tradePrice(trade.getOrderPrice().getValue().doubleValue())
                .tradeVolume(trade.getQuantity().getValue().doubleValue())
                .prevClosingPrice(0.0)   // 내부 데이터에 없으니 기본값
                .changePrice(0.0)        // 내부 데이터에 없으니 기본값
                .askBid(trade.isBuyTrade() ? "BID" : "ASK")
                .sequentialId(UUIDSupport.uuidToLong(trade.getId().getValue())) // TradeId의 Long 값
                .build();
    }

}
