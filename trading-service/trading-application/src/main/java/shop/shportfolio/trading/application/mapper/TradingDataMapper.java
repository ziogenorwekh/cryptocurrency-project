package shop.shportfolio.trading.application.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderResponse;
import shop.shportfolio.trading.application.command.create.CreateReservationResponse;
import shop.shportfolio.trading.application.command.track.response.*;
import shop.shportfolio.trading.application.command.update.CancelOrderResponse;
import shop.shportfolio.trading.application.dto.marketdata.candle.CandleDayResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.candle.CandleMinuteResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.candle.CandleMonthResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.candle.CandleWeekResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.ticker.MarketTickerResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.trade.TradeTickResponseDto;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.entity.orderbook.MarketItem;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TradingDataMapper {

    public CreateLimitOrderResponse limitOrderToCreateLimitOrderResponse(LimitOrder limitOrder) {
        return new CreateLimitOrderResponse(
                limitOrder.getUserId().getValue(),limitOrder.getId().getValue() ,limitOrder.getMarketId().getValue(), limitOrder.getOrderPrice().getValue()
                , limitOrder.getOrderSide().getValue(), limitOrder.getQuantity().getValue(), limitOrder.getOrderType()
        );
    }

    public OrderBookTrackResponse orderBookToOrderBookTrackResponse(OrderBook orderBook) {
        List<OrderBookBidsResponse> bids = orderBook.getBuyPriceLevels().values().stream()
                .map(priceLevel -> {
                    String price = priceLevel.getTickPrice().getValue().toPlainString();
                    String quantity = priceLevel.getOrders().stream()
                            .map(order -> order.getRemainingQuantity().getValue())
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .toPlainString();
                    return new OrderBookBidsResponse(price, quantity);
                })
                .collect(Collectors.toList());

        List<OrderBookAsksResponse> asks = orderBook.getSellPriceLevels().values().stream()
                .map(priceLevel -> {
                    String price = priceLevel.getTickPrice().getValue().toPlainString();
                    String quantity = priceLevel.getOrders().stream()
                            .map(order -> order.getRemainingQuantity().getValue())
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .toPlainString();
                    return new OrderBookAsksResponse(price, quantity);
                })
                .collect(Collectors.toList());

        return new OrderBookTrackResponse(
                orderBook.getId().getValue(),
                bids,
                asks
        );
    }

    public LimitOrderTrackResponse limitOrderTrackToLimitOrderTrackResponse(LimitOrder limitOrder) {
        return new LimitOrderTrackResponse(limitOrder.getUserId().getValue(), limitOrder.getMarketId().getValue(),
                limitOrder.getOrderSide(), limitOrder.getOrderStatus(), limitOrder.getRemainingQuantity().getValue()
                , limitOrder.getOrderPrice().getValue());
    }


    public CreateReservationResponse reservationOrderToCreateReservationResponse(ReservationOrder reservationOrder) {
        return CreateReservationResponse
                .builder()
                .orderId(reservationOrder.getId().getValue())
                .expireAt(reservationOrder.getExpireAt().getValue())
                .status(reservationOrder.getOrderStatus().name())
                .scheduledTime(reservationOrder.getScheduledTime().getValue())
                .build();
    }

    public CancelOrderResponse limitOrderToCancelOrderResponse(LimitOrder limitOrder) {
        return CancelOrderResponse.builder()
                .orderId(limitOrder.getId().getValue())
                .orderStatus(limitOrder.getOrderStatus())
                .build();
    }

    public CancelOrderResponse reservationOrderToCancelOrderResponse(ReservationOrder reservationOrder) {
        return CancelOrderResponse.builder()
                .orderId(reservationOrder.getId().getValue())
                .orderStatus(reservationOrder.getOrderStatus())
                .build();
    }

    public ReservationOrderTrackResponse reservationOrderToReservationOrderTrackResponse(ReservationOrder order) {
        return ReservationOrderTrackResponse.builder()
                .orderId(order.getId().getValue())
                .userId(order.getUserId().getValue())
                .triggerType(order.getTriggerCondition().getValue().name())
                .isRepeatable(order.getIsRepeatable().isRepeatable())
                .targetPrice(order.getTriggerCondition().getTargetPrice().getValue())
                .expireAt(order.getExpireAt().getValue())
                .quantity(order.getRemainingQuantity().getValue())
                .scheduledTime(order.getScheduledTime().getValue())
                .build();
    }

    public MarketCodeTrackResponse marketItemToMarketItemTrackResponse(MarketItem marketItem) {
        return MarketCodeTrackResponse.builder()
                .marketId(marketItem.getId().getValue())
                .marketEnglishName(marketItem.getMarketEnglishName().getValue())
                .marketKoreanName(marketItem.getMarketKoreanName().getValue())
                .build();
    }

    public CandleMinuteTrackResponse candleMinuteResponseDtoToCandleMinuteTrackResponse(CandleMinuteResponseDto dto) {
        return new CandleMinuteTrackResponse(
                dto.getMarketId(),
                dto.getCandleDateTimeKST(),
                dto.getOpeningPrice(),
                dto.getHighPrice(),
                dto.getLowPrice(),
                dto.getTradePrice(),
                dto.getTimestamp(),
                dto.getCandleAccTradePrice(),
                dto.getCandleAccTradeVolume(),
                dto.getUnit()
        );
    }

    public CandleDayTrackResponse candleDayResponseDtoToCandleDayTrackResponse(CandleDayResponseDto dto) {
        return new CandleDayTrackResponse(
                dto.getMarket(),
                dto.getCandleDateTimeUtc(),
                dto.getCandleDateTimeKst(),
                dto.getOpeningPrice(),
                dto.getHighPrice(),
                dto.getLowPrice(),
                dto.getTradePrice(),
                dto.getCandleAccTradePrice(),
                dto.getCandleAccTradeVolume(),
                dto.getPrevClosingPrice(),
                dto.getChangePrice(),
                dto.getChangeRate()
        );
    }

    public CandleWeekTrackResponse candleWeekResponseDtoToCandleWeekTrackResponse(CandleWeekResponseDto dto) {
        return new CandleWeekTrackResponse(
                dto.getMarket(),
                dto.getCandleDateTimeUtc(),
                dto.getCandleDateTimeKst(),
                dto.getOpeningPrice(),
                dto.getHighPrice(),
                dto.getLowPrice(),
                dto.getTradePrice(),
                dto.getTimestamp(),
                dto.getCandleAccTradePrice(),
                dto.getCandleAccTradeVolume(),
                dto.getFirstDayOfPeriod()
        );
    }


    public CandleMonthTrackResponse candleMonthResponseDtoToCandleMonthTrackResponse(CandleMonthResponseDto dto) {
        return new CandleMonthTrackResponse(
                dto.getMarket(),
                dto.getCandleDateTimeUtc(),
                dto.getCandleDateTimeKst(),
                dto.getOpeningPrice(),
                dto.getHighPrice(),
                dto.getLowPrice(),
                dto.getTradePrice(),
                dto.getTimestamp(),
                dto.getCandleAccTradePrice(),
                dto.getCandleAccTradeVolume(),
                dto.getFirstDayOfPeriod()
        );
    }

    public TickerTrackResponse marketTickerResponseDtoToTickerTrackResponse(MarketTickerResponseDto dto) {
        return new TickerTrackResponse(
                dto.getMarket(),
                dto.getTradeDate(),
                dto.getTradeTime(),
                dto.getTradeDateKst(),
                dto.getTradeTimeKst(),
                dto.getTradeTimestamp(),
                dto.getOpeningPrice(),
                dto.getHighPrice(),
                dto.getLowPrice(),
                dto.getTradePrice(),
                dto.getPrevClosingPrice(),
                dto.getChange(),
                dto.getChangePrice(),
                dto.getChangeRate(),
                dto.getSignedChangePrice(),
                dto.getSignedChangeRate(),
                dto.getTradeVolume(),
                dto.getAccTradePrice(),
                dto.getAccTradePrice24h(),
                dto.getAccTradeVolume(),
                dto.getAccTradeVolume24h(),
                dto.getHighest52WeekPrice(),
                dto.getHighest52WeekDate(),
                dto.getLowest52WeekPrice(),
                dto.getLowest52WeekDate(),
                dto.getTimestamp()
        );
    }

    public TradeTickResponse tradeTickResponseDtoToTradeTickResponse(TradeTickResponseDto dto) {
        return new TradeTickResponse(
                dto.getMarket(),
                dto.getTradeDateUtc(),
                dto.getTradeTimeUtc(),
                dto.getTimestamp(),
                dto.getTradePrice(),
                dto.getTradeVolume(),
                dto.getPrevClosingPrice(),
                dto.getChangePrice(),
                dto.getAskBid(),
                dto.getSequentialId()
        );
    }
}
