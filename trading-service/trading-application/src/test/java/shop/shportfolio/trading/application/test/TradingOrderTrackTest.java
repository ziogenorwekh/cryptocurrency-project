package shop.shportfolio.trading.application.test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.command.track.request.*;
import shop.shportfolio.trading.application.command.track.response.*;
import shop.shportfolio.trading.application.dto.marketdata.MarketItemBithumbDto;
import shop.shportfolio.trading.application.dto.marketdata.candle.CandleDayResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.candle.CandleMinuteResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.candle.CandleMonthResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.candle.CandleWeekResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.ticker.MarketTickerResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.trade.TradeTickResponseDto;
import shop.shportfolio.trading.application.exception.OrderNotFoundException;
import shop.shportfolio.trading.application.ports.input.*;
import shop.shportfolio.trading.application.ports.output.kafka.TradeKafkaPublisher;
import shop.shportfolio.trading.application.ports.output.marketdata.BithumbApiPort;
import shop.shportfolio.trading.application.ports.output.redis.TradingMarketDataRedisPort;
import shop.shportfolio.trading.application.ports.output.redis.TradingOrderRedisPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingCouponRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingMarketDataRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingTradeRecordRepositoryPort;
import shop.shportfolio.trading.application.scheduler.MarketHardCodingData;
import shop.shportfolio.trading.application.test.factory.*;
import shop.shportfolio.trading.application.test.helper.MarketDataApplicationTestHelper;
import shop.shportfolio.trading.application.test.helper.TestConstants;
import shop.shportfolio.trading.application.test.helper.TradingOrderTestHelper;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketItem;
import shop.shportfolio.trading.domain.entity.Trade;
import shop.shportfolio.trading.domain.valueobject.OrderSide;
import shop.shportfolio.trading.domain.valueobject.OrderType;
import shop.shportfolio.trading.domain.valueobject.TradeId;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;


@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
public class TradingOrderTrackTest {

    private TradingApplicationService tradingApplicationService;
    private MarketDataApplicationService marketDataApplicationService;

    @Mock private TradingOrderRepositoryPort tradingOrderRepositoryPort;
    @Mock private TradingTradeRecordRepositoryPort tradingTradeRecordRepositoryPort;
    @Mock private TradingOrderRedisPort tradingOrderRedisPort;
    @Mock private BithumbApiPort bithumbApiPort;
    @Mock private TradingMarketDataRedisPort tradingMarketDataRedisPort;
    @Mock private TradeKafkaPublisher tradeKafkaPublisher;
    @Mock private TradingCouponRepositoryPort tradingCouponRepositoryPort;
    @Mock private TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort;

    private final UUID userId = TestConstants.TEST_USER_ID;
    private final String marketId = TestConstants.TEST_MARKET_ID;

    private final LimitOrder limitOrder = LimitOrder.createLimitOrder(
            new UserId(userId),
            new MarketId(marketId),
            OrderSide.BUY,
            new Quantity(BigDecimal.valueOf(1.0)),
            new OrderPrice(BigDecimal.valueOf(1_050_000.0)),
            OrderType.LIMIT
    );

    @BeforeEach
    void setUp() {
        tradingApplicationService = TradingOrderTestHelper.createTradingApplicationService(
                tradingOrderRepositoryPort,
                tradingTradeRecordRepositoryPort,
                tradingOrderRedisPort,
                tradingMarketDataRepositoryPort,
                tradingMarketDataRedisPort,
                tradingCouponRepositoryPort,
                tradeKafkaPublisher,
                bithumbApiPort
        );

        marketDataApplicationService = MarketDataApplicationTestHelper.createMarketDataApplicationService(
                tradingOrderRepositoryPort,
                tradingTradeRecordRepositoryPort,
                tradingOrderRedisPort,
                tradingMarketDataRepositoryPort,
                tradingMarketDataRedisPort,
                bithumbApiPort);

    }


    @Test
    @DisplayName("오더 아이디로 주문 조회 테스트")
    public void cancelNonExistingOrderThrowsException() {
        // given
        LimitOrderTrackQuery limitOrderTrackQuery = new LimitOrderTrackQuery(limitOrder.getId().getValue(), userId);
        Mockito.when(tradingOrderRepositoryPort.findLimitOrderByOrderIdAndUserId(limitOrder.getId().getValue(),
                        limitOrder.getUserId().getValue()))
                .thenReturn(Optional.of(limitOrder));
        // when
        LimitOrderTrackResponse track = tradingApplicationService.findLimitOrderTrackByOrderIdAndUserId(limitOrderTrackQuery);
        // then
        Assertions.assertNotNull(track);
        Assertions.assertEquals(track.getOrderPrice(), limitOrder.getOrderPrice().getValue());
        Assertions.assertEquals(track.getOrderStatus(), limitOrder.getOrderStatus());
        Assertions.assertEquals(track.getUserId(), userId);
        Assertions.assertEquals(track.getMarketId(), marketId);
    }

    @Test
    @DisplayName("존재하지 않는 주문 ID로 주문 취소 시 예외 처리 테스트")
    public void trackOrderButNotFoundThrowsException() {
        // given
        LimitOrderTrackQuery limitOrderTrackQuery = new LimitOrderTrackQuery("anonymous", userId);
        // when
        OrderNotFoundException orderNotFoundException = Assertions.assertThrows(OrderNotFoundException.class, () ->
                tradingApplicationService.findLimitOrderTrackByOrderIdAndUserId(limitOrderTrackQuery));
        // then
        Assertions.assertNotNull(orderNotFoundException);
        Assertions.assertEquals("Order with id " +
                "anonymous" + " not found", orderNotFoundException.getMessage());
    }

    @Test
    @DisplayName("마켓 전체 조회 테스트")
    public void retrieveAllMarketTest() {
        // given
        List<MarketItem> marketItems = new ArrayList<>();
        List<MarketItemBithumbDto> mockMarketList = MarketItemTestFactory.createMockMarketList();
        List<Map.Entry<String, Integer>> marketMapEntries = new ArrayList<>(MarketHardCodingData.marketMap.entrySet());

        for (int i = 0; i < marketMapEntries.size(); i++) {
            Map.Entry<String, Integer> entry = marketMapEntries.get(i);
            MarketItemBithumbDto dto = mockMarketList.get(i);

            MarketItem entity = MarketDataApplicationTestHelper.tradingDtoMapper.marketItemBithumbDtoToMarketItem(dto, entry.getValue());
            marketItems.add(entity);
        }
        Mockito.when(tradingMarketDataRepositoryPort.findAllMarketItems()).thenReturn(marketItems);
        // when
        List<MarketCodeTrackResponse> allMarkets = marketDataApplicationService.findAllMarkets();
        // then
        Assertions.assertNotNull(allMarkets);
        Assertions.assertFalse(allMarkets.isEmpty());
        System.out.println("allMarkets.size() = " + allMarkets.size());
        Assertions.assertEquals(mockMarketList.size(),allMarkets.size());
        Mockito.verify(tradingMarketDataRepositoryPort,Mockito.times(1)).findAllMarketItems();
    }


    @Test
    @DisplayName("분봉 조회 테스트")
    public void retrieveMinuteCandleTest() {
        // given
        List<CandleMinuteResponseDto> mockMinuteCandles = CandleMinuteTestFactory.createMockMinuteCandles();
        Mockito.when(bithumbApiPort.findCandleMinutes(Mockito.any())).thenReturn(mockMinuteCandles);
        // when
        List<CandleMinuteTrackResponse> candleMinute = marketDataApplicationService
                .findCandleMinute(new CandleMinuteTrackQuery());
        // then
        Assertions.assertNotNull(candleMinute);
        Assertions.assertEquals(mockMinuteCandles.size(), candleMinute.size());
        Mockito.verify(bithumbApiPort,Mockito.times(1)).findCandleMinutes(Mockito.any());
    }

    @Test
    @DisplayName("일봉 조회 테스트")
    public void retrieveDayCandleTest() {
        // given
        List<CandleDayResponseDto> mockDayCandles = CandleDayTestFactory.createMockDayCandles();
        Mockito.when(bithumbApiPort.findCandleDays(Mockito.any())).thenReturn(mockDayCandles);
        // when
        List<CandleDayTrackResponse> candleDay = marketDataApplicationService
                .findCandleDay(new CandleTrackQuery());
        // then
        Assertions.assertNotNull(candleDay);
        Assertions.assertEquals(mockDayCandles.size(), candleDay.size());
        Mockito.verify(bithumbApiPort, Mockito.times(1)).findCandleDays(Mockito.any());
    }

    @Test
    @DisplayName("주봉 조회 테스트")
    public void retrieveWeekCandleTest() {
        // given
        List<CandleWeekResponseDto> mockWeekCandles = CandleWeekTestFactory.createMockWeekCandles();
        Mockito.when(bithumbApiPort.findCandleWeeks(Mockito.any())).thenReturn(mockWeekCandles);
        // when
        List<CandleWeekTrackResponse> candleWeek = marketDataApplicationService
                .findCandleWeek(new CandleTrackQuery());
        // then
        Assertions.assertNotNull(candleWeek);
        Assertions.assertEquals(mockWeekCandles.size(), candleWeek.size());
        Mockito.verify(bithumbApiPort, Mockito.times(1)).findCandleWeeks(Mockito.any());
    }

    @Test
    @DisplayName("월봉 조회 테스트")
    public void retrieveMonthCandleTest() {
        // given
        List<CandleMonthResponseDto> mockMonthCandles = CandleMonthTestFactory.createMockMonthCandles();
        Mockito.when(bithumbApiPort.findCandleMonths(Mockito.any())).thenReturn(mockMonthCandles);
        // when
        List<CandleMonthTrackResponse> candleMonth = marketDataApplicationService
                .findCandleMonth(new CandleTrackQuery());
        // then
        Assertions.assertNotNull(candleMonth);
        Assertions.assertEquals(mockMonthCandles.size(), candleMonth.size());
        Mockito.verify(bithumbApiPort, Mockito.times(1)).findCandleMonths(Mockito.any());
    }


//    내 체결내역과 내 현재가도 조합해서 조회해야 됌
    @Test
    @DisplayName("현재가 Ticker 조회 테스트")
    public void retrieveTickerTest() {
        // given
        MarketTickerResponseDto mockTicker = MarketTickerTestFactory.createMockTicker().get(0);
        Mockito.when(bithumbApiPort.findTickerByMarketId(Mockito.any())).thenReturn(mockTicker);
        // when
        TickerTrackResponse marketTicker = marketDataApplicationService.findMarketTicker(new TickerTrackQuery());
        // then
        Assertions.assertNotNull(marketTicker);
        Assertions.assertEquals("KRW-BTC",marketTicker.getMarket());
        Mockito.verify(tradingTradeRecordRepositoryPort, Mockito.times(1))
                .findTopByMarketIdOrderByCreatedAtDesc(Mockito.any());
        Mockito.verify(bithumbApiPort, Mockito.times(1)).findTickerByMarketId(Mockito.any());
    }

    @Test
    @DisplayName("현재가 Ticker 조회 테스트인데 내 거래가 더 빠른경우에는 내꺼가 보여야 됌")
    public void retrieveTickerButWhenMyTradeRecordFasterThanExternalAPIRecordsTest() {
        // given
        Trade nowTrade = Trade.createTrade(new TradeId(UUID.randomUUID()), new MarketId(marketId),new UserId(userId), OrderId.anonymous()
                , new OrderPrice(BigDecimal.valueOf(1_000_000_0)), new Quantity(BigDecimal.valueOf(2L)),
                TransactionType.TRADE_BUY, null, null);
        MarketTickerResponseDto mockTicker = MarketTickerTestFactory.createMockTicker().get(0);
        Mockito.when(bithumbApiPort.findTickerByMarketId(Mockito.any())).thenReturn(mockTicker);
        Mockito.when(tradingTradeRecordRepositoryPort.findTopByMarketIdOrderByCreatedAtDesc(Mockito.any()))
                .thenReturn(Optional.of(nowTrade));
        // when
        TickerTrackResponse marketTicker = marketDataApplicationService.findMarketTicker(new TickerTrackQuery());
        // then
        Assertions.assertNotNull(marketTicker);
        Assertions.assertEquals("KRW-BTC",marketTicker.getMarket());
        Assertions.assertEquals(10000000,marketTicker.getTradePrice());
        Assertions.assertEquals(nowTrade.getCreatedAt().getValue().
                format(DateTimeFormatter.ofPattern("HHmmss")), marketTicker.getTradeTime());
        Assertions.assertEquals(nowTrade.getQuantity().getValue().doubleValue(), marketTicker.getTradeVolume());

        Mockito.verify(tradingTradeRecordRepositoryPort, Mockito.times(1))
                .findTopByMarketIdOrderByCreatedAtDesc(Mockito.any());
        Mockito.verify(bithumbApiPort, Mockito.times(1)).findTickerByMarketId(Mockito.any());
    }
//    내 거래내역도 시간대별로 파악해서 포함해서 보내줘야 됌
    @Test
    @DisplayName("최근 체결 내역 조회 테스트(내 데이터 없이)")
    public void retrieveTradeTickerTest() {
        // given
        List<TradeTickResponseDto> mockTradeTicks = TradeTickTestFactory.createMockTradeTicks();
        Mockito.when(bithumbApiPort.findTradeTicks(Mockito.any())).thenReturn(mockTradeTicks);
        Mockito.when(tradingTradeRecordRepositoryPort
                .findTradesByMarketIdAndCreatedAtBetween(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any()))
                .thenReturn(List.of());
        // when
        List<TradeTickResponse> tradeTick = marketDataApplicationService.findTradeTick(new TradeTickTrackQuery());
        // then
        Assertions.assertNotNull(tradeTick);
        Assertions.assertEquals(mockTradeTicks.size(), tradeTick.size());
        Mockito.verify(tradingTradeRecordRepositoryPort, Mockito.times(1))
                .findTradesByMarketIdAndCreatedAtBetween(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any());
        Mockito.verify(bithumbApiPort, Mockito.times(1)).findTradeTicks(Mockito.any());
    }

    @Test
    @DisplayName("최근 체결 내역 조회 테스트(이번엔 내 데이터가 있음)")
    public void retrieveTradeTickerWithMyTradingServiceDataTest() {
        // given - 더미 Trade 리스트 생성
        MarketId marketId = new MarketId("KRW-BTC");
        UserId userId = new UserId(UUID.randomUUID());
        OrderId buyOrderId = OrderId.anonymous();
        Trade trade = Trade.createTrade(
                new TradeId(UUID.randomUUID()),
                marketId,
                userId,
                buyOrderId,
                new OrderPrice(BigDecimal.valueOf(160_000_000)),
                new Quantity(BigDecimal.valueOf(0.001)),
                TransactionType.TRADE_BUY,
                new FeeAmount(BigDecimal.ZERO),
                new FeeRate(BigDecimal.ZERO)
        );

        List<Trade> myTrades = List.of(trade);

        Mockito.when(bithumbApiPort.findTradeTicks(Mockito.any())).thenReturn(TradeTickTestFactory.createMockTradeTicks());
        Mockito.when(tradingTradeRecordRepositoryPort
                        .findTradesByMarketIdAndCreatedAtBetween(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(myTrades);

        // when
        List<TradeTickResponse> result = marketDataApplicationService.findTradeTick(new TradeTickTrackQuery());

        // then
        Assertions.assertNotNull(result);

        // 외부 API + 내부 DB 데이터 합쳐진 수 만큼 기대
        int expectedSize = myTrades.size() + TradeTickTestFactory.createMockTradeTicks().size();
        Assertions.assertEquals(expectedSize, result.size());

        Mockito.verify(tradingTradeRecordRepositoryPort, Mockito.times(1))
                .findTradesByMarketIdAndCreatedAtBetween(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(bithumbApiPort, Mockito.times(1)).findTradeTicks(Mockito.any());
    }
}
