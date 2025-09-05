package shop.shportfolio.trading.domain.test;


import org.junit.jupiter.api.*;
import org.springframework.test.util.ReflectionTestUtils;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.OrderDomainService;
import shop.shportfolio.trading.domain.OrderDomainServiceImpl;
import shop.shportfolio.trading.domain.TradeDomainService;
import shop.shportfolio.trading.domain.TradeDomainServiceImpl;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.entity.orderbook.PriceLevel;
import shop.shportfolio.trading.domain.entity.trade.Trade;
import shop.shportfolio.trading.domain.event.LimitOrderCreatedEvent;
import shop.shportfolio.trading.domain.exception.TradingDomainException;
import shop.shportfolio.trading.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class OrderDomainServiceTest {


    private OrderBook orderBook;
    private LimitOrder testBuyOrder;
    private LimitOrder testLimitOrder;

    private final MarketId marketId = new MarketId("BTC-KRW");
    private final MarketItemTick marketItemTick = new MarketItemTick(BigDecimal.valueOf(100_000));
    private OrderDomainService orderDomainService;
    private TradeDomainService tradeDomainService;

    @BeforeEach
    public void setUp() {
        orderBook = new OrderBook(marketId);
        tradeDomainService = new TradeDomainServiceImpl();
        orderDomainService = new OrderDomainServiceImpl();

        int basePrice = 11_000_000;
        int maxPrice = 11_800_000;
        int step = 100_000;

        int priceLevelsCount = ((maxPrice - basePrice) / step) + 1; // 9개 레벨

        int ordersPerLevel = 100;

        for (int priceLevelIndex = 0; priceLevelIndex < priceLevelsCount; priceLevelIndex++) {
            BigDecimal price = BigDecimal.valueOf(basePrice + (long) step * priceLevelIndex);

            // 매수 주문 100개 생성 및 추가
            for (int i = 0; i < ordersPerLevel; i++) {
                UserId userId = new UserId(UUID.randomUUID());
                LimitOrder buyOrder = LimitOrder.createLimitOrder(
                        userId,
                        marketId,
                        OrderSide.BUY,
                        new Quantity(BigDecimal.ONE),
                        new OrderPrice(price),
                        OrderType.LIMIT
                );
                orderBook.addOrder(buyOrder); // 매수 주문일 경우 즉시 오더북에 추가
            }

            // 매도 주문 100개 생성 및 추가
            for (int i = 0; i < ordersPerLevel; i++) {
                UserId userId = new UserId(UUID.randomUUID());
                LimitOrder sellOrder = LimitOrder.createLimitOrder(
                        userId,
                        marketId,
                        OrderSide.SELL,
                        new Quantity(BigDecimal.ONE),
                        new OrderPrice(price),
                        OrderType.LIMIT
                );
                orderBook.addOrder(sellOrder); // 매도 주문일 경우 즉시 오더북에 추가
            }
        }

        testBuyOrder = LimitOrder.createLimitOrder(
                new UserId(UUID.randomUUID()),
                marketId,
                OrderSide.BUY,
                new Quantity(BigDecimal.ONE),
                new OrderPrice(BigDecimal.valueOf(1_000_000)),
                OrderType.LIMIT
        );

        testLimitOrder = LimitOrder.createLimitOrder(
                new UserId(UUID.randomUUID()),
                marketId,
                OrderSide.BUY,
                new Quantity(BigDecimal.TEN),
                new OrderPrice(BigDecimal.valueOf(11_400_000)),
                OrderType.LIMIT
        );
    }


    @Test
    @DisplayName("지정가 주문 추가 시 원본 가격을 유지하지 않고 절삭된 가격이 반영되었는지, PriceLevel은 절삭 가격으로 저장되는지 검증")
    public void shouldAddLimitOrderWithOriginalPriceAndTruncatedPriceLevel() {
        // given
        UserId userId = new UserId(UUID.randomUUID());
        MarketId marketId = new MarketId("BTC-KRW");

        BigDecimal rawPriceValue = BigDecimal.valueOf(11_400_220);
        OrderPrice rawPrice = new OrderPrice(rawPriceValue);
        TickPrice tickPrice = TickPrice.of(rawPriceValue, marketItemTick.getValue());

        LimitOrder limitOrder = LimitOrder.createLimitOrder(
                userId,
                marketId,
                OrderSide.BUY,
                new Quantity(BigDecimal.ONE),
                rawPrice,
                OrderType.LIMIT
        );

        // when
        orderBook.addOrder(limitOrder);

        // then
        Assertions.assertEquals(101L, orderBook.getBidsSizeByTickPrice(tickPrice));

        PriceLevel priceLevel = orderBook.getBuyPriceLevels().get(tickPrice);
        Assertions.assertNotNull(priceLevel);

        boolean found = priceLevel.getOrders().stream()
                .anyMatch(order -> order.getUserId().equals(userId));
        Assertions.assertTrue(found);

        Assertions.assertTrue(orderBook.getBuyPriceLevels().containsKey(tickPrice));
    }


    @Test
    @DisplayName("열린 주문을 취소할 때 상태가 CANCELED로 변경되는지 확인")
    public void cancelOrderSuccessTest() {
        // given
        // when
        orderDomainService.cancelOrder(testBuyOrder);
        // then
        Assertions.assertFalse(testBuyOrder.isOpen());
        Assertions.assertEquals(OrderStatus.CANCELED, testBuyOrder.getOrderStatus());
    }

    @Test
    @DisplayName("체결된 주문 취소 시도 시 예외 발생 테스트")
    public void cancelOrderAlreadyFilledFailTest() {
        // given && when
        Quantity result = orderDomainService.applyOrder(testBuyOrder, new Quantity(BigDecimal.ONE));
        TradingDomainException tradingDomainException = Assertions.assertThrows(TradingDomainException.class,
                () -> testBuyOrder.cancel());
        // then
        Assertions.assertEquals(BigDecimal.valueOf(0L),testBuyOrder.getRemainingQuantity().getValue());
        Assertions.assertEquals("Order already completed or canceled",
                tradingDomainException.getMessage());
        Assertions.assertEquals(OrderStatus.FILLED, testBuyOrder.getOrderStatus());
    }

    @Test
    @DisplayName("체결 후 남은 수량이 정확히 줄어드는지 테스트")
    public void applyOrderReducesRemainingQtyTest() {
        // given & when
        Quantity quantity = orderDomainService.applyOrder(testLimitOrder, new Quantity(BigDecimal.ONE));
//        testLimitOrder.applyTrade(new Quantity(BigDecimal.ONE));
        // then
        Assertions.assertEquals(BigDecimal.valueOf(9L), testLimitOrder.getRemainingQuantity().getValue());
        Assertions.assertEquals(OrderStatus.PARTIALLY_FILLED, testLimitOrder.getOrderStatus());

    }

    @Test
    @DisplayName("남은 수량 0 시 주문 상태가 FILLED로 바뀌는지 확인")
    public void applyOrderFillsOrderLimitAndReservationTest() {
        // given && when
        Quantity result = orderDomainService.applyOrder(testBuyOrder, new Quantity(BigDecimal.ONE));
        System.out.println(result.getValue());
//        testBuyOrder.applyTrade(new Quantity(BigDecimal.ONE));
        // then
        Assertions.assertEquals(BigDecimal.valueOf(0L),testBuyOrder.getRemainingQuantity().getValue());
        Assertions.assertEquals(OrderStatus.FILLED, testBuyOrder.getOrderStatus());
        Assertions.assertEquals(OrderType.LIMIT, testBuyOrder.getOrderType());
    }

    @Test
    @DisplayName("남은 수량 없을 때 유효성 검증 예외 발생 테스트")
    public void validatePlaceableThrowsOnZeroQtyOnlyTest() {
        // given
        Quantity result = orderDomainService.applyOrder(testBuyOrder, new Quantity(BigDecimal.ONE));
        ReflectionTestUtils.setField(testBuyOrder, "orderStatus", OrderStatus.OPEN);
        // when
        TradingDomainException exception = Assertions.assertThrows(
                TradingDomainException.class,
                () -> testBuyOrder.validatePlaceable()
        );
        // then
        Assertions.assertEquals(BigDecimal.valueOf(0L),testBuyOrder.getRemainingQuantity().getValue());
        Assertions.assertEquals("Order has no remaining quantity.", exception.getMessage());
    }

    @Test
    @DisplayName("매수/매도 서로 다른 주문끼리 매칭 가능 여부 확인")
    public void canMatchWithOppositeOrderTest() {
        // given
        LimitOrder sellOrder = LimitOrder.createLimitOrder(new UserId(UUID.randomUUID()), marketId,
                OrderSide.SELL, new Quantity(BigDecimal.valueOf(2L))
                , new OrderPrice(BigDecimal.valueOf(11_100_000)), OrderType.LIMIT);
        // when
        Boolean matchWith = orderDomainService.canMatchWith(testBuyOrder, sellOrder);
//        Boolean matchWith = testBuyOrder.canMatchWith(sellOrder);
        //then
        Assertions.assertTrue(matchWith);
    }

    @Test
    @DisplayName("매수 주문 가격 매칭 조건 정상 동작 확인")
    public void isPriceMatchForBuyOrderTest() {
        // given
        LimitOrder buyOrder = LimitOrder.createLimitOrder(new UserId(UUID.randomUUID()), marketId,
                OrderSide.BUY, new Quantity(BigDecimal.TEN)
                , new OrderPrice(BigDecimal.valueOf(1_100_000)), OrderType.LIMIT);

        // when
        Boolean matchEqual = orderDomainService.isPriceMatch(buyOrder, new OrderPrice(BigDecimal.valueOf(1_100_000)));
        Boolean matchLower = orderDomainService.isPriceMatch(buyOrder, new OrderPrice(BigDecimal.valueOf(1_000_000)));
        Boolean matchHigher = orderDomainService.isPriceMatch(buyOrder, new OrderPrice(BigDecimal.valueOf(1_200_000)));
        // then
        Assertions.assertTrue(matchEqual);
        Assertions.assertTrue(matchLower);
        Assertions.assertFalse(matchHigher);
    }

    @Test
    @DisplayName("매도 주문 가격 매칭 조건 정상 동작 확인")
    public void isPriceMatchForSellOrderTest() {
        // given
        LimitOrder sellOrder = LimitOrder.createLimitOrder(new UserId(UUID.randomUUID()), marketId,
                OrderSide.SELL, new Quantity(BigDecimal.TEN)
                , new OrderPrice(BigDecimal.valueOf(1_100_000)), OrderType.LIMIT);

        // when
        Boolean matchEqual = orderDomainService.isPriceMatch(sellOrder, new OrderPrice(BigDecimal.valueOf(1_100_000)));
        Boolean matchLower = orderDomainService.isPriceMatch(sellOrder, new OrderPrice(BigDecimal.valueOf(1_000_000)));
        Boolean matchHigher = orderDomainService.isPriceMatch(sellOrder, new OrderPrice(BigDecimal.valueOf(1_200_000)));
        // then
        Assertions.assertTrue(matchEqual);
        Assertions.assertTrue(matchHigher);
        Assertions.assertFalse(matchLower);
    }

    @Test
    @DisplayName("실행 수량이 남은 수량 초과 시 예외 발생 테스트")
    public void rejectApplyOrderIfExecutedQtyTooLargeTest() {
        // given
        LimitOrder buyOrder = LimitOrder.createLimitOrder(new UserId(UUID.randomUUID()), marketId,
                OrderSide.BUY, new Quantity(BigDecimal.TEN)
                , new OrderPrice(BigDecimal.valueOf(1_000_000)), OrderType.LIMIT);
        // when
        Quantity result = orderDomainService.applyOrder(buyOrder, new Quantity(BigDecimal.valueOf(11)));
        // then
        Assertions.assertFalse(result.isZero());
    }

    @Test
    @DisplayName("부분 체결 시 남은 수량이 업데이트되는지 테스트")
    public void partialFillUpdatesRemainingQtyTest() {
        // given && when
//        testLimitOrder.applyTrade(new Quantity(BigDecimal.valueOf(7L)));
        orderDomainService.applyOrder(testLimitOrder,new Quantity(BigDecimal.valueOf(7)));
        // then
        Assertions.assertEquals(OrderStatus.PARTIALLY_FILLED, testLimitOrder.getOrderStatus());
        Assertions.assertTrue(testLimitOrder.isBuyOrder());
        Assertions.assertEquals(BigDecimal.valueOf(3L), testLimitOrder.getRemainingQuantity().getValue());
    }

    @Test
    @DisplayName("주문 상태 변경 흐름이 올바른지 검증")
    public void orderStatusTransitionTest() {
        // given && when
        orderDomainService.applyOrder(testBuyOrder,new Quantity(BigDecimal.valueOf(1L)));
        orderDomainService.applyOrder(testLimitOrder,new Quantity(BigDecimal.valueOf(2L)));
//        testBuyOrder.applyTrade(new Quantity(BigDecimal.valueOf(1L)));
//        testLimitOrder.applyTrade(new Quantity(BigDecimal.valueOf(2L)));
        // then
        Assertions.assertEquals(OrderStatus.PARTIALLY_FILLED, testLimitOrder.getOrderStatus());
        Assertions.assertEquals(OrderStatus.FILLED, testBuyOrder.getOrderStatus());
    }

    @Test
    @DisplayName("isBuyOrder, isSellOrder 메서드 정상 동작 확인 테스트")
    public void orderSideCheckTest() {
        // given
        LimitOrder sellOrder = LimitOrder.createLimitOrder(new UserId(UUID.randomUUID()), marketId,
                OrderSide.SELL, new Quantity(BigDecimal.TEN)
                , new OrderPrice(BigDecimal.valueOf(1_100_000)), OrderType.LIMIT);
        // when
        Boolean isSell = orderDomainService.isSellOrder(sellOrder);
        Boolean isBuy = orderDomainService.isBuyOrder(testBuyOrder);
//        Boolean isSell = sellOrder.isSellOrder();
//        Boolean isBuy = testBuyOrder.isBuyOrder();
        // then
        Assertions.assertTrue(isSell);
        Assertions.assertTrue(isBuy);
    }

    @Test
    @DisplayName("주문서에 주문 추가가 정상 동작하는지 테스트")
    public void orderBookAddOrderTest() {
        // given
        LimitOrderCreatedEvent limitOrderCreatedEvent = orderDomainService.createLimitOrder(new UserId(UUID.randomUUID()),
                marketId, OrderSide.of("BUY"),
                new Quantity(BigDecimal.valueOf(2L)), new OrderPrice(BigDecimal.valueOf(11_100_000)), OrderType.LIMIT);
        LimitOrder limitOrder = limitOrderCreatedEvent.getDomainType();

        // when
        OrderBook added = orderDomainService.addOrderbyOrderBook(orderBook, limitOrder);
        // then
        Assertions.assertNotNull(added);
        Assertions.assertEquals(101L,added.getBidsSizeByTickPrice(new TickPrice(BigDecimal.valueOf(11_100_000))));
    }

    @Test
    @DisplayName("최우선 매도 호가 조회 정상 작동 테스트")
    public void getBestAskPriceLevelTest() {
        // when
        Map.Entry<TickPrice, PriceLevel> bestAskEntry = orderBook.getSellPriceLevels().firstEntry();

        // then
        Assertions.assertNotNull(bestAskEntry, "최우선 매도 호가가 존재해야 한다");
        TickPrice bestAskPrice = bestAskEntry.getKey();
        PriceLevel bestAskLevel = bestAskEntry.getValue();

        Assertions.assertNotNull(bestAskPrice, "최우선 매도 가격이 null 이면 안된다");
        Assertions.assertNotNull(bestAskLevel, "최우선 매도 가격레벨이 null 이면 안된다");

        Assertions.assertFalse(bestAskLevel.getOrders().isEmpty(), "최우선 매도 주문 큐는 비어있으면 안된다");
    }
    @Test
    @DisplayName("최우선 매수 호가 조회 정상 작동 테스트")
    public void getBestBidPriceLevelTest() {
        // when
        Map.Entry<TickPrice, PriceLevel> bestBidEntry = orderBook.getBuyPriceLevels().firstEntry();

        // then
        Assertions.assertNotNull(bestBidEntry, "최우선 매수 호가가 존재해야 한다");
        TickPrice bestBidPrice = bestBidEntry.getKey();
        PriceLevel bestBidLevel = bestBidEntry.getValue();

        Assertions.assertNotNull(bestBidPrice, "최우선 매수 가격이 null 이면 안된다");
        Assertions.assertNotNull(bestBidLevel, "최우선 매수 가격레벨이 null 이면 안된다");

        Assertions.assertFalse(bestBidLevel.getOrders().isEmpty(), "최우선 매수 주문 큐는 비어있으면 안된다");
    }
    @Test
    @DisplayName("오더북에 기존의 거래내역이 있다면 삭제하는 테스트")
    void deleteOrderBookItemByTradeTest() {
        // given
        TickPrice tickPrice = TickPrice.of(BigDecimal.valueOf(11_000_000), marketItemTick.getValue());
        long initialOrderCount = orderBook.getBidsSizeByTickPrice(tickPrice);
        Assertions.assertTrue(initialOrderCount >= 3, "초기 주문 수량은 최소 3 이상이어야 함");

        PriceLevel priceLevel = orderBook.getSellPriceLevels().get(tickPrice);
        Assertions.assertNotNull(priceLevel, "PriceLevel은 존재해야 함");

        int initialQueueSize = priceLevel.getOrders().size();
        Assertions.assertEquals(initialOrderCount, initialQueueSize, "PriceLevel 주문 큐 크기와 getBidsSizeByTickPrice 일치");

        Order firstOrder = priceLevel.peekOrder();
        Assertions.assertNotNull(firstOrder, "첫 번째 주문은 존재해야 함");
        Quantity firstRemainingBefore = firstOrder.getRemainingQuantity();

        Trade trade = new Trade(
                new TradeId(UUID.randomUUID()),
                marketId,
                new UserId(UUID.randomUUID()),
                new OrderId("Anonymous"),
                new OrderId("Anonymous"),
                new OrderPrice(tickPrice.getValue()),
                new Quantity(BigDecimal.valueOf(3)),   // 주문 수량 3개 차감 예정
                TransactionType.TRADE_BUY,
                new CreatedAt(LocalDateTime.now(ZoneOffset.UTC).plusMinutes(5)),
                new FeeAmount(BigDecimal.valueOf(1250L)),
                new FeeRate(BigDecimal.valueOf(0.2))
        );

        // when
        tradeDomainService.applyExecutedTrade(orderBook, trade);

        // then
        long afterOrderCount = orderBook.getAsksSizeByTickPrice(tickPrice);
        Assertions.assertEquals(97, afterOrderCount, "주문 수량이 3만큼 줄어야 함");

        int afterQueueSize = priceLevel.getOrders().size();
        Assertions.assertEquals(afterOrderCount, afterQueueSize, "PriceLevel 주문 큐 크기도 감소해야 함");

        Order firstOrderAfter = priceLevel.peekOrder();
        if (firstOrderAfter != null) {
            Quantity firstRemainingAfter = firstOrderAfter.getRemainingQuantity();
            Assertions.assertTrue(
                    firstRemainingAfter.getValue().compareTo(firstRemainingBefore.getValue()) <= 0,
                    "첫 주문의 남은 수량이 줄어들었거나 같아야 함"
            );
        }

        for (Order order : priceLevel.getOrders()) {
            Assertions.assertTrue(order.getRemainingQuantity().isPositive(), "남은 주문은 수량이 0보다 커야 함");
            Assertions.assertTrue(order.isOpen(), "남은 주문 상태는 OPEN이어야 함");
        }
    }

    @Test
    @DisplayName("체결하고 남은 수량은 어느정도인지 테스트")
    public void applyOrderTest() {
        // given
        LimitOrderCreatedEvent limitOrderCreatedEvent = orderDomainService.createLimitOrder(new UserId(UUID.randomUUID()),
                marketId, OrderSide.of("BUY"),
                new Quantity(BigDecimal.valueOf(2L)), new OrderPrice(BigDecimal.valueOf(11_100_000)), OrderType.LIMIT);
        LimitOrder limitOrder = limitOrderCreatedEvent.getDomainType();
        // when 2L 주문에 1.4개만 주문 받으면 남은 수량은 0.6개
        Quantity quantity = orderDomainService.applyOrder(limitOrder, new Quantity(BigDecimal.valueOf(1.4)));
        // then
        Assertions.assertEquals(BigDecimal.valueOf(0.6), limitOrder.getRemainingQuantity().getValue(), " 1.4개만 주문 받았으니 남은 수량은" +
                "0.6개여야 함");
        Assertions.assertEquals(BigDecimal.valueOf(2L), limitOrder.getQuantity().getValue());
    }

    @Test
    @DisplayName("오더 주문량보다 실제 체결가격이 많으면 0이 나오고 FILLED로 바뀌어야 하는 테스트")
    public void applyOrderQuantityMoreThanExecQuantityTest() {
        // given
        LimitOrderCreatedEvent limitOrderCreatedEvent = orderDomainService.createLimitOrder(new UserId(UUID.randomUUID()),
                marketId, OrderSide.of("BUY"),
                new Quantity(BigDecimal.valueOf(2L)), new OrderPrice(BigDecimal.valueOf(11_100_000)), OrderType.LIMIT);
        LimitOrder limitOrder = limitOrderCreatedEvent.getDomainType();
        // when 2L 주문에 1.4개만 주문 받으면 남은 수량은 0.6개
        Quantity quantity = orderDomainService.applyOrder(limitOrder, new Quantity(BigDecimal.valueOf(3.5)));
        // then
        Assertions.assertEquals(BigDecimal.valueOf(2L), quantity.getValue());
        Assertions.assertEquals(OrderStatus.FILLED, limitOrder.getOrderStatus());
    }
}