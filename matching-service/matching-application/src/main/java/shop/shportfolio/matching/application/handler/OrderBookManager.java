package shop.shportfolio.matching.application.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.matching.application.command.OrderBookTrackResponse;
import shop.shportfolio.matching.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.matching.application.helper.MarketHardCodingData;
import shop.shportfolio.matching.application.mapper.MatchingDataMapper;
import shop.shportfolio.matching.application.mapper.MatchingDtoMapper;
import shop.shportfolio.matching.application.ports.output.repository.ExternalOrderBookStore;
import shop.shportfolio.matching.application.ports.output.repository.OrderStore;
import shop.shportfolio.matching.application.ports.output.socket.OrderBookSocketClient;
import shop.shportfolio.matching.application.ports.input.socket.OrderBookListener;
import shop.shportfolio.matching.application.ports.output.socket.OrderBookSender;
import shop.shportfolio.matching.domain.entity.MatchingOrderBook;
import shop.shportfolio.trading.domain.entity.LimitOrder;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

@Component
public class OrderBookManager implements OrderBookListener {

    private final OrderBookSocketClient orderBookSocketClient;
    private final MatchingDtoMapper matchingDtoMapper;
    private final ExternalOrderBookStore externalOrderBookStore;
    private final OrderStore orderStore;
    private final MatchingDataMapper matchingDataMapper;
    private final OrderBookSender orderBookSender;

    @Autowired
    public OrderBookManager(OrderBookSocketClient client, MatchingDtoMapper mapper,
                            ExternalOrderBookStore externalOrderBookStore,
                            OrderStore orderStore, MatchingDataMapper matchingDataMapper,
                            OrderBookSender orderBookSender) {
        this.orderBookSocketClient = client;
        this.matchingDtoMapper = mapper;
        this.externalOrderBookStore = externalOrderBookStore;
        this.orderStore = orderStore;
        this.matchingDataMapper = matchingDataMapper;
        this.orderBookSender = orderBookSender;
        this.orderBookSocketClient.setOrderBookListener(this);
        orderBookSocketClient.connect();
        MarketHardCodingData.marketMap.keySet().forEach(orderBookSocketClient::subscribeMarket);
    }

    @Override
    public void onOrderBookReceived(OrderBookBithumbDto dto) {
        MatchingOrderBook matchingOrderBook = matchingDtoMapper
                .orderBookDtoToOrderBook(dto);
        loadAdjustedOrderBook(matchingOrderBook);
        MatchingOrderBook adjusted = externalOrderBookStore.getOrderBook(matchingOrderBook.getId().getValue());

        OrderBookTrackResponse response = matchingDataMapper
                .orderBookToOrderBookTrackResponse(adjusted);

        orderBookSender.send(response);
    }

    private void loadAdjustedOrderBook(MatchingOrderBook matchingOrderBook) {
        Queue<LimitOrder> limitOrders = orderStore.getLimitOrders(matchingOrderBook.getId().getValue());

        Set<String> existingOrderIds = new HashSet<>();
        matchingOrderBook.getBuyPriceLevels().values().forEach(priceLevel ->
                priceLevel.getOrders().forEach(order -> existingOrderIds.add(order.getId().getValue()))
        );
        matchingOrderBook.getSellPriceLevels().values().forEach(priceLevel ->
                priceLevel.getOrders().forEach(order -> existingOrderIds.add(order.getId().getValue()))
        );
        LimitOrder limitOrder;
        while ((limitOrder = limitOrders.poll()) != null) {
            if (!existingOrderIds.contains(limitOrder.getId().getValue())) {
                matchingOrderBook.addOrder(limitOrder);
                existingOrderIds.add(limitOrder.getId().getValue());
            }
        }
        externalOrderBookStore.putOrderBook(matchingOrderBook.getId().getValue(),
                matchingOrderBook);
    }
}