package shop.shportfolio.trading.application.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.trading.application.mapper.TradingDtoMapper;
import shop.shportfolio.trading.application.orderbook.memorystore.ExternalOrderBookMemoryStore;
import shop.shportfolio.trading.application.ports.output.marketdata.BithumbApiPort;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;

import java.math.BigDecimal;

@Slf4j
@Component
public class OrderBookScheduler {
    private final BithumbApiPort bithumbApiPort;
    private final TradingDtoMapper tradingDtoMapper;

    public OrderBookScheduler(BithumbApiPort bithumbApiPort,
                              TradingDtoMapper tradingDtoMapper) {
        this.bithumbApiPort = bithumbApiPort;
        this.tradingDtoMapper = tradingDtoMapper;
    }


    @Scheduled(fixedDelayString = "${update.orderbook.scheduler.interval-ms}")
    public void updateOrderBook() {
        MarketHardCodingData.marketMap.forEach((market, tickPrice) -> {
            try {
                OrderBookBithumbDto externalOrderBook = bithumbApiPort.findOrderBookByMarketId(market);
                OrderBook orderBook = tradingDtoMapper
                        .orderBookDtoToOrderBook(externalOrderBook, BigDecimal.valueOf(tickPrice));
                ExternalOrderBookMemoryStore.getInstance().putOrderBook(market, orderBook);
                log.debug("OrderBook loaded into memory: {}", market);
            } catch (Exception ex) {
                log.error("Failed to update OrderBook for market: {}", market, ex);
            }
        });
    }

}
