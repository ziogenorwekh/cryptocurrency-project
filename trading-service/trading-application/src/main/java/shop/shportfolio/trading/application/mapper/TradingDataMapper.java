package shop.shportfolio.trading.application.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderResponse;
import shop.shportfolio.trading.application.command.create.CreateReservationResponse;
import shop.shportfolio.trading.application.command.track.LimitOrderTrackResponse;
import shop.shportfolio.trading.application.command.track.OrderBookAsksResponse;
import shop.shportfolio.trading.application.command.track.OrderBookBidsResponse;
import shop.shportfolio.trading.application.command.track.OrderBookTrackResponse;
import shop.shportfolio.trading.domain.entity.*;

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
}
