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
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

@Component
public class OrderBookManager implements OrderBookListener {

    private final BithumbSocketClient bithumbSocketClient;
    private final MatchingDtoMapper matchingDtoMapper;
    private final ExternalOrderBookMemoryStore externalOrderBookMemoryStore;

    @Autowired
    public OrderBookManager(BithumbSocketClient client, MatchingDtoMapper mapper) {
        this.bithumbSocketClient = client;
        this.matchingDtoMapper = mapper;
        externalOrderBookMemoryStore = ExternalOrderBookMemoryStore.getInstance();
        this.bithumbSocketClient.setOrderBookListener(this);
    }

    @PostConstruct
    private void start() {
        bithumbSocketClient.connect();
    }

    // 외부에서 DTO로 들어온 호가 데이터를 메모리 스토어에 넣기
    @Override
    public void onOrderBookReceived(OrderBookBithumbDto dto, double marketItemTick) {
        String marketId = dto.getMarket();
        OrderBook orderBook = matchingDtoMapper.orderBookDtoToOrderBook(dto, BigDecimal.valueOf(marketItemTick));
        externalOrderBookMemoryStore.putOrderBook(marketId, orderBook);
    }

    public OrderBook loadAdjustedOrderBook(String marketId,OrderMemoryStore orderMemoryStore) {
        OrderBook orderBook = externalOrderBookMemoryStore.getOrderBook(marketId);
        Queue<LimitOrder> limitOrders = orderMemoryStore.getAllLimitOrders();
        Set<String> existingOrderIds = new HashSet<>();
        orderBook.getBuyPriceLevels().values().forEach(priceLevel ->
                priceLevel.getOrders().forEach(order -> existingOrderIds.add(order.getId().getValue()))
        );
        orderBook.getSellPriceLevels().values().forEach(priceLevel ->
                priceLevel.getOrders().forEach(order -> existingOrderIds.add(order.getId().getValue()))
        );
        limitOrders.forEach(limitOrder -> {
            if (!existingOrderIds.contains(limitOrder.getId().getValue())) {
                orderBook.addOrder(limitOrder);
                existingOrderIds.add(limitOrder.getId().getValue());
            }
        });
        return orderBook;
    }



}
