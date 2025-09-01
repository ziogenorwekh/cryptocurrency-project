package shop.shportfolio.matching.application.handler;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.matching.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.matching.application.mapper.MatchingDtoMapper;
import shop.shportfolio.matching.application.memorystore.ExternalOrderBookMemoryStore;
import shop.shportfolio.matching.application.memorystore.OrderMemoryStore;
import shop.shportfolio.matching.application.ports.output.socket.BithumbSocketClient;
import shop.shportfolio.matching.application.ports.input.socket.OrderBookListener;
import shop.shportfolio.matching.domain.entity.MatchingOrderBook;
import shop.shportfolio.trading.domain.entity.LimitOrder;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

@Component
public class OrderBookManager implements OrderBookListener {

    private final BithumbSocketClient bithumbSocketClient;
    private final MatchingDtoMapper matchingDtoMapper;
    private final ExternalOrderBookMemoryStore externalOrderBookMemoryStore;
    private final OrderMemoryStore orderMemoryStore;
    @Autowired
    public OrderBookManager(BithumbSocketClient client, MatchingDtoMapper mapper,
                            ExternalOrderBookMemoryStore externalOrderBookMemoryStore,
                            OrderMemoryStore orderMemoryStore) {
        this.bithumbSocketClient = client;
        this.matchingDtoMapper = mapper;
        this.externalOrderBookMemoryStore = externalOrderBookMemoryStore;
        this.orderMemoryStore = orderMemoryStore;
        this.bithumbSocketClient.setOrderBookListener(this);
    }

    @PostConstruct
    private void start() {
        bithumbSocketClient.connect();
    }

    // 외부에서 DTO로 들어온 호가 데이터를 메모리 스토어에 넣기
    @Override
    public void onOrderBookReceived(OrderBookBithumbDto dto, Long marketItemTick) {
        String marketId = dto.getMarket();
        MatchingOrderBook matchingOrderBook = matchingDtoMapper
                .orderBookDtoToOrderBook(dto, BigDecimal.valueOf(marketItemTick));
        externalOrderBookMemoryStore.putOrderBook(marketId, matchingOrderBook);
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
