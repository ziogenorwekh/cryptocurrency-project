package shop.shportfolio.trading.application.test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import shop.shportfolio.common.domain.exception.DomainException;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.application.command.update.CancelLimitOrderCommand;
import shop.shportfolio.trading.application.command.update.CancelOrderResponse;
import shop.shportfolio.trading.application.command.update.CancelReservationOrderCommand;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookAsksBithumbDto;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBidsBithumbDto;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.trading.application.ports.input.*;
import shop.shportfolio.trading.application.ports.output.kafka.TradeKafkaPublisher;
import shop.shportfolio.trading.application.ports.output.marketdata.BithumbApiPort;
import shop.shportfolio.trading.application.ports.output.redis.TradingMarketDataRedisPort;
import shop.shportfolio.trading.application.ports.output.redis.TradingOrderRedisPort;
import shop.shportfolio.trading.application.ports.output.repository.*;
import shop.shportfolio.trading.application.test.helper.TestConstants;
import shop.shportfolio.trading.application.test.helper.TradingOrderTestHelper;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.orderbook.MarketItem;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
public class TradingOrderCancelTest {

    private TradingApplicationService tradingApplicationService;

    @Mock private TradingOrderRepositoryPort tradingOrderRepositoryPort;
    @Mock private TradingTradeRecordRepositoryPort tradingTradeRecordRepositoryPort;
    @Mock private TradingOrderRedisPort tradingOrderRedisPort;
    @Mock private TradingMarketDataRedisPort tradingMarketDataRedisPort;
    @Mock private TradeKafkaPublisher tradeKafkaPublisher;
    @Mock private TradingCouponRepositoryPort tradingCouponRepositoryPort;
    @Mock private TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort;
    @Mock private BithumbApiPort bithumbApiPort;
    @Mock private TradingUserBalanceRepositoryPort tradingUserBalanceRepositoryPort;
    @Captor ArgumentCaptor<ReservationOrder> reservationOrderCaptor;

    private final UUID userId = TestConstants.TEST_USER_ID;
    private final String marketId = TestConstants.TEST_MARKET_ID;
    private final MarketStatus marketStatus = TestConstants.MARKET_STATUS;
    private OrderBookBithumbDto orderBookBithumbDto;

    private TradingOrderTestHelper helper;
    @BeforeEach
    public void setUp() {
        tradingApplicationService = helper.createTradingApplicationService(
                tradingOrderRepositoryPort,
                tradingTradeRecordRepositoryPort,
                tradingOrderRedisPort,
                tradingMarketDataRepositoryPort,
                tradingMarketDataRedisPort,
                tradingCouponRepositoryPort,
                tradeKafkaPublisher,
                bithumbApiPort,
                tradingUserBalanceRepositoryPort
        );
        orderBookBithumbDto = new OrderBookBithumbDto();
        orderBookBithumbDto.setMarket(marketId);
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
    }

    @Test
    @DisplayName("주문 취소 테스트")
    public void cancelLimitOrderAndRestoreOrderBook() {
        // given
//        List<LimitOrder> limitOrders = new ArrayList<>();
        LimitOrder limitOrder = LimitOrder.createLimitOrder(new UserId(userId), new MarketId(marketId),
                OrderSide.BUY, new Quantity(BigDecimal.valueOf(2)),
                new OrderPrice(BigDecimal.valueOf(10_500_000.0)), OrderType.LIMIT);
//        limitOrders.add(limitOrder);
//        Mockito.when(tradingOrderRedisPort.findLimitOrdersByMarketId(marketId)).thenReturn(limitOrders);
        Mockito.when(tradingOrderRepositoryPort.
                        findLimitOrderByOrderIdAndUserId(Mockito.any(),Mockito.any()))
                .thenReturn(Optional.of(limitOrder));
        LimitOrder saved = LimitOrder.createLimitOrder(new UserId(userId), new MarketId(marketId),
                OrderSide.BUY, new Quantity(BigDecimal.valueOf(2)),
                new OrderPrice(BigDecimal.valueOf(10_500_000.0)), OrderType.LIMIT);
        saved.cancel();
        Mockito.when(tradingOrderRepositoryPort.saveLimitOrder(Mockito.any())).thenReturn(saved);
        CancelLimitOrderCommand command = new CancelLimitOrderCommand(limitOrder.getId().getValue(), userId, marketId);
        // when
        CancelOrderResponse response = tradingApplicationService.cancelLimitOrder(command);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(OrderStatus.CANCELED, response.getOrderStatus());
        Mockito.verify(tradingOrderRepositoryPort, Mockito.times(1))
                .saveLimitOrder(Mockito.any());
        Mockito.verify(tradingOrderRedisPort, Mockito.times(1))
                .deleteLimitOrder(Mockito.any());
    }

    @Test
    @DisplayName("리밋 오더 취소하는데, 이미 거래가 완료된 경우 테스트")
    public void cannotCancelLimitOrderTest() {
        // given
        LimitOrder limitOrder = new LimitOrder(new UserId(userId), new MarketId(marketId),
                OrderSide.BUY, new Quantity(BigDecimal.ONE),
                new OrderPrice(BigDecimal.valueOf(10_500_000.0)), OrderType.LIMIT);
        limitOrder.applyTrade(new Quantity(BigDecimal.ONE));
        Mockito.when(tradingOrderRepositoryPort.findLimitOrderByOrderIdAndUserId(Mockito.any(),Mockito.any()))
                .thenReturn(Optional.of(limitOrder));
        // when
        DomainException domainException = Assertions.assertThrows(DomainException.class, () -> {
            tradingApplicationService.cancelLimitOrder(new CancelLimitOrderCommand(limitOrder.getId().getValue(),
                    userId, marketId));
        });
        // then
        Assertions.assertNotNull(domainException);
        Assertions.assertEquals("Order already completed or canceled",domainException.getMessage());
    }

    @Test
    @DisplayName("예약 주문 취소 테스트")
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void cancelReservationOrderTest() {
        // given
        BigDecimal price = BigDecimal.valueOf(10_500_000.0);
        LocalDateTime scheduledTime = LocalDateTime.now(ZoneOffset.UTC).plusDays(1);
        LocalDateTime expireAt = LocalDateTime.now(ZoneOffset.UTC).plusMonths(1);
        ReservationOrder reservationOrder = ReservationOrder.
                createReservationOrder(new UserId(userId), new MarketId(marketId), OrderSide.BUY
                        , new Quantity(BigDecimal.valueOf(2)), OrderType.RESERVATION, TriggerCondition.of(TriggerType.ABOVE,
                                new OrderPrice(price)), ScheduledTime.of(scheduledTime), new ExpireAt(expireAt),
                        IsRepeatable.of(true));
        Mockito.when(tradingOrderRepositoryPort.findReservationOrderByOrderIdAndUserId(Mockito.any(),
                        Mockito.any()))
                .thenReturn(Optional.of(reservationOrder));
        MarketItem marketItem = MarketItem.createMarketItem(marketId, new MarketKoreanName("비트코인"),
                new MarketEnglishName("BTC"), new MarketWarning(""),
                new TickPrice(BigDecimal.valueOf(1000L)),marketStatus);
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(marketItem));
        // when
        helper.tradingUpdateUseCase.cancelReservationOrder(new CancelReservationOrderCommand(
                reservationOrder.getId().getValue(), userId, marketId));
        Mockito.verify(tradingOrderRepositoryPort,
                Mockito.times(1)).saveReservationOrder(reservationOrderCaptor.capture());
        ReservationOrder captured = reservationOrderCaptor.getValue();
        // then
        Assertions.assertEquals(OrderStatus.CANCELED,captured.getOrderStatus());
    }


    // 편의 메서드
    private OrderBookAsksBithumbDto createAsk(Double price, Double size) {
        OrderBookAsksBithumbDto ask = new OrderBookAsksBithumbDto();
        ask.setAskPrice(price);
        ask.setAskSize(size);
        return ask;
    }

    private OrderBookBidsBithumbDto createBid(Double price, Double size) {
        OrderBookBidsBithumbDto bid = new OrderBookBidsBithumbDto();
        bid.setBidPrice(price);
        bid.setBidSize(size);
        return bid;
    }

}
