package shop.shportfolio.trading.domain.test;


import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.OrderBook;
import shop.shportfolio.trading.domain.entity.PriceLevel;
import shop.shportfolio.trading.domain.valueobject.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
public class TradingDomainServiceTest {


    private List<Order> buyOrders;
    private List<Order> sellOrders;
    private OrderBook orderBook;

    @BeforeEach
    public void setUp() {
        MarketId marketId = new MarketId("BTC-KRW");
        orderBook = new OrderBook(marketId);

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
        Assertions.assertEquals(orderBook.getSizeByPriceLevelPrice(priceLevelPrice),223);
        Optional<Order> orderOptional = orderBook.getBuyPriceLevels().get(priceLevelPrice).getBuyOrders()
                .stream().filter(order -> order.getUserId().getValue().equals(userId.getValue())).findAny();
        Assertions.assertTrue(orderOptional.isPresent());
        Assertions.assertEquals(priceLevelPrice.getValue(),orderOptional.get().getOrderPrice().getValue());
    }


    @Test
    @DisplayName("열린 주문을 취소할 때 상태가 CANCELED로 변경되는지 확인")
    public void cancelOrderSuccessTest() {

    }

    @Test
    @DisplayName("체결된 주문 취소 시도 시 예외 발생 테스트")
    public void cancelOrderAlreadyFilledFailTest() {

    }

    @Test
    @DisplayName("체결 후 남은 수량이 정확히 줄어드는지 테스트")
    public void applyTradeReducesRemainingQtyTest() {

    }

    @Test
    @DisplayName("남은 수량 0 시 주문 상태가 FILLED로 바뀌는지 확인")
    public void applyTradeFillsOrderTest() {

    }

    @Test
    @DisplayName("남은 수량 없거나 0일 때 유효성 검증 예외 발생 테스트")
    public void validatePlaceableThrowsOnZeroQtyTest() {

    }

    @Test
    @DisplayName("매수/매도 서로 다른 주문끼리 매칭 가능 여부 확인")
    public void canMatchWithOppositeOrderTest() {

    }

    @Test
    @DisplayName("매수 주문 가격 매칭 조건 정상 동작 확인")
    public void isPriceMatchForBuyOrderTest() {

    }

    @Test
    @DisplayName("매도 주문 가격 매칭 조건 정상 동작 확인")
    public void isPriceMatchForSellOrderTest() {

    }

    @Test
    @DisplayName("실행 수량이 남은 수량 초과 시 예외 발생 테스트")
    public void rejectApplyTradeIfExecutedQtyTooLargeTest() {

    }

    @Test
    @DisplayName("부분 체결 시 남은 수량 올바른 업데이트 테스트")
    public void partialFillUpdatesRemainingQtyTest() {

    }

    @Test
    @DisplayName("주문 상태 변경 흐름이 올바른지 검증")
    public void orderStatusTransitionTest() {

    }

    @Test
    @DisplayName("isBuyOrder, isSellOrder 메서드 정상 동작 확인 테스트")
    public void orderSideCheckTest() {

    }

    @Test
    @DisplayName("주문서에 주문 추가가 정상 동작하는지 테스트")
    public void orderBookAddOrderTest() {

    }

    @Test
    @DisplayName("주문서에서 최우선 매수/매도 호가 조회 정상 작동 테스트")
    public void orderBookGetBestBidAskTest() {

    }

    private int countAllBuyOrders() {
        return orderBook.getBuyPriceLevels()
                .values().stream()
                .mapToInt(priceLevel -> priceLevel.getBuyOrders().size())
                .sum();
    }

}
