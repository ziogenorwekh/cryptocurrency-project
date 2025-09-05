package shop.shportfolio.matching.application.handler;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.matching.application.command.OrderBookTrackResponse;
import shop.shportfolio.matching.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.matching.application.helper.MarketHardCodingData;
import shop.shportfolio.matching.application.mapper.MatchingDataMapper;
import shop.shportfolio.matching.application.mapper.MatchingDtoMapper;
import shop.shportfolio.matching.application.memorystore.ExternalOrderBookMemoryStore;
import shop.shportfolio.matching.application.memorystore.OrderMemoryStore;
import shop.shportfolio.matching.application.ports.output.socket.BithumbSocketClient;
import shop.shportfolio.matching.application.ports.input.socket.OrderBookListener;
import shop.shportfolio.matching.application.ports.output.socket.OrderBookSender;
import shop.shportfolio.matching.domain.entity.MatchingOrderBook;
import shop.shportfolio.trading.domain.entity.LimitOrder;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

@Component
public class OrderBookManager implements OrderBookListener {

    private final BithumbSocketClient bithumbSocketClient;
    private final MatchingDtoMapper matchingDtoMapper;
    private final ExternalOrderBookMemoryStore externalOrderBookMemoryStore;
    private final OrderMemoryStore orderMemoryStore;
    private final MatchingDataMapper matchingDataMapper;
    private final OrderBookSender orderBookSender;

    @Autowired
    public OrderBookManager(BithumbSocketClient client, MatchingDtoMapper mapper,
                            ExternalOrderBookMemoryStore externalOrderBookMemoryStore,
                            OrderMemoryStore orderMemoryStore, MatchingDataMapper matchingDataMapper,
                            OrderBookSender orderBookSender) {
        this.bithumbSocketClient = client;
        this.matchingDtoMapper = mapper;
        this.externalOrderBookMemoryStore = externalOrderBookMemoryStore;
        this.orderMemoryStore = orderMemoryStore;
        this.matchingDataMapper = matchingDataMapper;
        this.orderBookSender = orderBookSender;
        this.bithumbSocketClient.setOrderBookListener(this);
    }

    @PostConstruct
    private void start() {
        bithumbSocketClient.connect();
        // 모든 마켓 구독
        MarketHardCodingData.marketMap.keySet().forEach(bithumbSocketClient::subscribeMarket);
    }

    // 외부에서 DTO로 들어온 호가 데이터를 메모리 스토어에 넣기
    @Override
    public void onOrderBookReceived(OrderBookBithumbDto dto) {
        String marketId = dto.getMarket();
        MatchingOrderBook matchingOrderBook = matchingDtoMapper
                .orderBookDtoToOrderBook(dto);
        externalOrderBookMemoryStore.putOrderBook(marketId, matchingOrderBook);
        MatchingOrderBook loadAdjustedOrderBook = loadAdjustedOrderBook(marketId);
        OrderBookTrackResponse orderBookTrackResponse = matchingDataMapper
                .orderBookToOrderBookTrackResponse(loadAdjustedOrderBook);
        orderBookSender.send(orderBookTrackResponse);
    }

    public MatchingOrderBook loadAdjustedOrderBook(String marketId) {
        MatchingOrderBook matchingOrderBook = externalOrderBookMemoryStore.getOrderBook(marketId);
        Queue<LimitOrder> limitOrders = orderMemoryStore.getAllLimitOrders();
        Set<String> existingOrderIds = new HashSet<>();
        matchingOrderBook.getBuyPriceLevels().values().forEach(priceLevel ->
                priceLevel.getOrders().forEach(order -> existingOrderIds.add(order.getId().getValue()))
        );
        matchingOrderBook.getSellPriceLevels().values().forEach(priceLevel ->
                priceLevel.getOrders().forEach(order -> existingOrderIds.add(order.getId().getValue()))
        );
        limitOrders.forEach(limitOrder -> {
            if (!existingOrderIds.contains(limitOrder.getId().getValue())) {
                matchingOrderBook.addOrder(limitOrder);
                existingOrderIds.add(limitOrder.getId().getValue());
            }
        });
        return matchingOrderBook;
    }
}