package shop.shportfolio.trading.domain.test;


import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.domain.TradingDomainService;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.OrderBook;
import shop.shportfolio.trading.domain.exception.TradingDomainException;
import shop.shportfolio.trading.domain.valueobject.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
public class TradingDomainServiceTest {


    private List<Order> buyOrders;
    private List<Order> sellOrders;
    private OrderBook orderBook;
    private LimitOrder testBuyOrder;
    private LimitOrder testLimitOrder;

    private MarketId marketId = new MarketId("BTC-KRW");
    private MarketItemTick marketItemTick = new MarketItemTick(BigDecimal.valueOf(100_000));
    private TradingDomainService tradingDomainService;

    @BeforeEach
    public void setUp() {
        orderBook = new OrderBook(marketId, marketItemTick);

        buyOrders = new ArrayList<>();
        sellOrders = new ArrayList<>();

        int basePrice = 11_000_000;
        int maxPrice = 11_800_000;
        int step = 100_000;
        // 매수매도 1000
        int totalOrders = 1000;
        // 9 가격 레벨
        int priceLevelsCount = ((maxPrice - basePrice) / step) + 1;
        // 가격 레벨별 주문 수량 (대략 동일하게 분배)
        int ordersPerLevel = totalOrders / priceLevelsCount;
        for (int i = 0; i < totalOrders; i++) {
            UserId userId = new UserId(UUID.randomUUID());
            int priceLevelIndex = i / ordersPerLevel; // 0 ~ 8
            // 남는 주문은 마지막 가격대에 몰기
            if (priceLevelIndex >= priceLevelsCount) priceLevelIndex = priceLevelsCount - 1;

            BigDecimal price = BigDecimal.valueOf(basePrice + step * priceLevelIndex);

            LimitOrder buyOrder = new LimitOrder(
                    userId,
                    marketId,
                    OrderSide.BUY,
                    new Quantity(BigDecimal.ONE),  // 수량 1
                    new OrderPrice(price),
                    OrderType.LIMIT
            );
            buyOrders.add(buyOrder);
        }
        for (int i = 0; i < totalOrders; i++) {
            UserId userId = new UserId(UUID.randomUUID());
            int priceLevelIndex = i / ordersPerLevel;
            if (priceLevelIndex >= priceLevelsCount) priceLevelIndex = priceLevelsCount - 1;

            BigDecimal price = BigDecimal.valueOf(basePrice + step * priceLevelIndex);

            LimitOrder sellOrder = new LimitOrder(
                    userId,
                    marketId,
                    OrderSide.SELL,
                    new Quantity(BigDecimal.ONE),
                    new OrderPrice(price),
                    OrderType.LIMIT
            );
            sellOrders.add(sellOrder);
        }
        buyOrders.forEach(orderBook::addOrder);
        sellOrders.forEach(orderBook::addOrder);

        testBuyOrder = new LimitOrder(
                new UserId(UUID.randomUUID()),
                marketId,
                OrderSide.BUY,
                new Quantity(BigDecimal.ONE),
                new OrderPrice(BigDecimal.valueOf(1_000_000)),
                OrderType.LIMIT
        );

        testLimitOrder = LimitOrder.createLimitOrder(new UserId(UUID.randomUUID()), marketId, OrderSide.BUY
                , new Quantity(BigDecimal.TEN), new OrderPrice(BigDecimal.valueOf(11_400_000)), OrderType.LIMIT);
    }


    @Test
    @DisplayName("지정가 주문 추가 시 원본 가격을 유지하지 않고 절삭된 가격이 반영되었는지," +
            " PriceLevel은 절삭 가격으로 저장되는지 검증")
    public void shouldAddLimitOrderWithOriginalPriceAndTruncatedPriceLevel() {
        // given
        UserId userId = new UserId(UUID.randomUUID());
        MarketId marketId = new MarketId("BTC-KRW");

        BigDecimal rawPriceValue = BigDecimal.valueOf(11_400_220);
        OrderPrice rawPrice = new OrderPrice(rawPriceValue);
        PriceLevelPrice priceLevelPrice = new PriceLevelPrice(BigDecimal.valueOf(11_400_000));
        LimitOrder limitOrder = new LimitOrder(
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
        Assertions.assertEquals(orderBook.getSizeByPriceLevelPrice(priceLevelPrice), 223);
        Optional<Order> orderOptional = orderBook.getBuyPriceLevels().get(priceLevelPrice).getBuyOrders()
                .stream().filter(order -> order.getUserId().getValue().equals(userId.getValue())).findAny();
        Assertions.assertTrue(orderOptional.isPresent());
        Assertions.assertEquals(priceLevelPrice.getValue(), orderOptional.get().getOrderPrice().getValue());
    }


    @Test
    @DisplayName("열린 주문을 취소할 때 상태가 CANCELED로 변경되는지 확인")
    public void cancelOrderSuccessTest() {
        // given
        // when
        testBuyOrder.cancel();
        // then
        Assertions.assertFalse(testBuyOrder.isOpen());
        Assertions.assertEquals(OrderStatus.CANCELED, testBuyOrder.getOrderStatus());
    }

    @Test
    @DisplayName("체결된 주문 취소 시도 시 예외 발생 테스트")
    public void cancelOrderAlreadyFilledFailTest() {
        // given
        testBuyOrder.applyTrade(new Quantity(BigDecimal.ONE));
        // when
        TradingDomainException tradingDomainException = Assertions.assertThrows(TradingDomainException.class,
                () -> testBuyOrder.cancel());
        // then
        Assertions.assertEquals("Order already completed or canceled",
                tradingDomainException.getMessage());
        Assertions.assertEquals(OrderStatus.FILLED, testBuyOrder.getOrderStatus());
    }

    @Test
    @DisplayName("체결 후 남은 수량이 정확히 줄어드는지 테스트")
    public void applyTradeReducesRemainingQtyTest() {
        // given & when
        testLimitOrder.applyTrade(new Quantity(BigDecimal.ONE));
        // then
        Assertions.assertEquals(BigDecimal.valueOf(9L), testLimitOrder.getRemainingQuantity().getValue());
        Assertions.assertEquals(OrderStatus.OPEN, testLimitOrder.getOrderStatus());

    }

    @Test
    @DisplayName("남은 수량 0 시 주문 상태가 FILLED로 바뀌는지 확인")
    public void applyTradeFillsOrderTest() {
        // given && when
        testBuyOrder.applyTrade(new Quantity(BigDecimal.ONE));
        // then
        Assertions.assertEquals(OrderStatus.FILLED, testBuyOrder.getOrderStatus());
        Assertions.assertEquals(OrderType.LIMIT, testBuyOrder.getOrderType());
    }

    @Test
    @DisplayName("남은 수량 없을 때 유효성 검증 예외 발생 테스트")
    public void validatePlaceableThrowsOnZeroQtyOnlyTest() {
        // given
        testBuyOrder.applyTrade(new Quantity(BigDecimal.valueOf(1)));
        ReflectionTestUtils.setField(testBuyOrder, "orderStatus", OrderStatus.OPEN);

        // when
        TradingDomainException exception = Assertions.assertThrows(
                TradingDomainException.class,
                () -> testBuyOrder.validatePlaceable()
        );
        // then
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
        Boolean matchWith = testBuyOrder.canMatchWith(sellOrder);
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
        Boolean matchEqual = buyOrder.isPriceMatch(new OrderPrice(BigDecimal.valueOf(1_100_000)));
        Boolean matchLower = buyOrder.isPriceMatch(new OrderPrice(BigDecimal.valueOf(1_000_000)));
        Boolean matchHigher = buyOrder.isPriceMatch(new OrderPrice(BigDecimal.valueOf(1_200_000)));
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
        Boolean matchEqual = sellOrder.isPriceMatch(new OrderPrice(BigDecimal.valueOf(1_100_000)));
        Boolean matchLower = sellOrder.isPriceMatch(new OrderPrice(BigDecimal.valueOf(1_000_000)));
        Boolean matchHigher = sellOrder.isPriceMatch(new OrderPrice(BigDecimal.valueOf(1_200_000)));
        // then
        Assertions.assertTrue(matchEqual);
        Assertions.assertTrue(matchHigher);
        Assertions.assertFalse(matchLower);
    }

    @Test
    @DisplayName("실행 수량이 남은 수량 초과 시 예외 발생 테스트")
    public void rejectApplyTradeIfExecutedQtyTooLargeTest() {
        // given
        LimitOrder buyOrder = LimitOrder.createLimitOrder(new UserId(UUID.randomUUID()), marketId,
                OrderSide.BUY, new Quantity(BigDecimal.TEN)
                , new OrderPrice(BigDecimal.valueOf(1_000_000)), OrderType.LIMIT);
        // when
        TradingDomainException tradingDomainException = Assertions.assertThrows(TradingDomainException.class,
                () -> buyOrder.applyTrade(new Quantity(BigDecimal.valueOf(11L))));
        // then

        Assertions.assertNotNull(tradingDomainException);
        Assertions.assertEquals("Executed quantity exceeds remaining quantity.",
                tradingDomainException.getMessage());
    }

    @Test
    @DisplayName("부분 체결 시 남은 수량이 업데이트되는지 테스트")
    public void partialFillUpdatesRemainingQtyTest() {
        // given && when
        testLimitOrder.applyTrade(new Quantity(BigDecimal.valueOf(7L)));
        // then
        Assertions.assertEquals(OrderStatus.OPEN, testLimitOrder.getOrderStatus());
        Assertions.assertTrue(testLimitOrder.isBuyOrder());
        Assertions.assertEquals(BigDecimal.valueOf(3L), testLimitOrder.getRemainingQuantity().getValue());
    }

    @Test
    @DisplayName("주문 상태 변경 흐름이 올바른지 검증")
    public void orderStatusTransitionTest() {
        // given && when
        testBuyOrder.applyTrade(new Quantity(BigDecimal.valueOf(1L)));
        testLimitOrder.applyTrade(new Quantity(BigDecimal.valueOf(2L)));
        // then
        Assertions.assertEquals(OrderStatus.OPEN, testLimitOrder.getOrderStatus());
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
        Boolean isSell = sellOrder.isSellOrder();
        Boolean isBuy = testBuyOrder.isBuyOrder();
        // then
        Assertions.assertTrue(isSell);
        Assertions.assertTrue(isBuy);
    }

    @Test
    @DisplayName("주문서에 주문 추가가 정상 동작하는지 테스트")
    public void orderBookAddOrderTest() {

    }

    @Test
    @DisplayName("주문서에서 최우선 매수/매도 호가 조회 정상 작동 테스트")
    public void orderBookGetBestBidAskTest() {

    }

}
