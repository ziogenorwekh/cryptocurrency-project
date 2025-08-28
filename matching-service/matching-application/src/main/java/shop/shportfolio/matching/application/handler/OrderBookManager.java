package shop.shportfolio.matching.application.handler;

import shop.shportfolio.matching.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.matching.application.mapper.TradingDtoMapper;
import shop.shportfolio.matching.application.memorystore.ExternalOrderBookMemoryStore;
import shop.shportfolio.matching.application.ports.output.socket.BithumbSocketClient;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;

import java.math.BigDecimal;

public class OrderBookManager {

    private final BithumbSocketClient bithumbSocketClient;
    private final TradingDtoMapper tradingDtoMapper;
    private final ExternalOrderBookMemoryStore memoryStore = ExternalOrderBookMemoryStore.getInstance();

    public OrderBookManager(BithumbSocketClient client, TradingDtoMapper mapper) {
        this.bithumbSocketClient = client;
        this.tradingDtoMapper = mapper;
    }

    private void start() {
        bithumbSocketClient.connect();
    }

    // 외부에서 DTO로 들어온 호가 데이터를 메모리 스토어에 넣기
    public void onOrderBookReceived(OrderBookBithumbDto dto, double marketItemTick) {
        String marketId = dto.getMarket();
        OrderBook orderBook = tradingDtoMapper.orderBookDtoToOrderBook(dto, BigDecimal.valueOf(marketItemTick));
        memoryStore.putOrderBook(marketId, orderBook);
    }

    public OrderBook getOrderBook(String marketId) {
        return memoryStore.getOrderBook(marketId);
    }


}
