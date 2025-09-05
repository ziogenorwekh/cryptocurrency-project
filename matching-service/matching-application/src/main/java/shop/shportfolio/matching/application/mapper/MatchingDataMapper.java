package shop.shportfolio.matching.application.mapper;


import org.springframework.stereotype.Component;
import shop.shportfolio.matching.application.command.OrderBookAsksResponse;
import shop.shportfolio.matching.application.command.OrderBookBidsResponse;
import shop.shportfolio.matching.application.command.OrderBookTrackResponse;
import shop.shportfolio.matching.domain.entity.MatchingOrderBook;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MatchingDataMapper {

    public OrderBookTrackResponse orderBookToOrderBookTrackResponse(MatchingOrderBook orderBook) {
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

        return OrderBookTrackResponse.builder()
                .marketId(orderBook.getId().getValue())
                .totalAskSize(orderBook.getTotalAskPrice().getValue().toPlainString())
                .totalBidSize(orderBook.getTotalBidPrice().getValue().toPlainString())
                .orderBookAsksResponse(asks)
                .orderBookBidsResponse(bids)
                .build();
    }
}
