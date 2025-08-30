package shop.shportfolio.matching.application.test.helper;

import shop.shportfolio.matching.application.dto.orderbook.OrderBookAsksBithumbDto;
import shop.shportfolio.matching.application.dto.orderbook.OrderBookBidsBithumbDto;
import shop.shportfolio.matching.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.matching.application.mapper.MatchingDtoMapper;
import shop.shportfolio.matching.application.memorystore.ExternalOrderBookMemoryStore;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;

import java.math.BigDecimal;
import java.util.List;

public class OrderBookTestHelper {


    public static void createOrderBook() {
        OrderBookBithumbDto orderBookBithumbDto = new OrderBookBithumbDto();
        orderBookBithumbDto.setMarket(TestConstants.TEST_MARKET_ID);
        orderBookBithumbDto.setTimestamp(System.currentTimeMillis());
        orderBookBithumbDto.setTotalAskSize(5.0);
        orderBookBithumbDto.setTotalBidSize(3.0);

        // 매도 호가 리스트 (가격 상승 순으로)
        List<OrderBookAsksBithumbDto> asks = List.of(
                createAsk(1_050_000.0, 1.0),
                createAsk(1_060_000.0, 1.2),
                createAsk(1_070_000.0, 1.4),
                createAsk(1_080_000.0, 1.6),
                createAsk(1_090_000.0, 1.8),
                createAsk(1_100_000.0, 2.0),
                createAsk(1_110_000.0, 2.2),
                createAsk(1_120_000.0, 2.4),
                createAsk(1_130_000.0, 2.6),
                createAsk(1_140_000.0, 2.8)
        );
        orderBookBithumbDto.setAsks(asks);

        // 매수 호가 리스트 (가격 하락 순으로)
        List<OrderBookBidsBithumbDto> bids = List.of(
                createBid(990_000.0, 1.0),
                createBid(980_000.0, 1.2),
                createBid(970_000.0, 1.4),
                createBid(960_000.0, 1.6),
                createBid(950_000.0, 1.8),
                createBid(940_000.0, 2.0),
                createBid(930_000.0, 2.2),
                createBid(920_000.0, 2.4),
                createBid(910_000.0, 2.6),
                createBid(900_000.0, 2.8)
        );
        orderBookBithumbDto.setBids(bids);
        MatchingDtoMapper tradingDtoMapper = new MatchingDtoMapper();
        OrderBook orderBook = tradingDtoMapper.orderBookDtoToOrderBook(orderBookBithumbDto,
                BigDecimal.valueOf(MarketHardCodingData
                .marketMap.get(TestConstants.TEST_MARKET_ID)));
        ExternalOrderBookMemoryStore.getInstance().putOrderBook(TestConstants.TEST_MARKET_ID, orderBook);
    }

    private static OrderBookAsksBithumbDto createAsk(Double price, Double size) {
        OrderBookAsksBithumbDto ask = new OrderBookAsksBithumbDto();
        ask.setAskPrice(price);
        ask.setAskSize(size);
        return ask;
    }

    private static OrderBookBidsBithumbDto createBid(Double price, Double size) {
        OrderBookBidsBithumbDto bid = new OrderBookBidsBithumbDto();
        bid.setBidPrice(price);
        bid.setBidSize(size);
        return bid;
    }

    public static OrderBook getOrderBook(String marketId) {
        return ExternalOrderBookMemoryStore.getInstance().getOrderBook(marketId);
    }

    public static void clear() {
        OrderBook orderBook = ExternalOrderBookMemoryStore
                .getInstance().getOrderBook(TestConstants.TEST_MARKET_ID);
        orderBook.getBuyPriceLevels().clear();
        orderBook.getSellPriceLevels().clear();
    }
}
