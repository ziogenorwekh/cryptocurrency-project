package shop.shportfolio.marketdata.insight.application.test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.shportfolio.marketdata.insight.application.MarketDataApplicationServiceImpl;
import shop.shportfolio.marketdata.insight.application.command.request.CandleMinuteTrackQuery;
import shop.shportfolio.marketdata.insight.application.command.request.CandleTrackQuery;
import shop.shportfolio.marketdata.insight.application.command.response.*;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleDayResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleMinuteResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleMonthResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleWeekResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.marketdata.MarketItemBithumbDto;
import shop.shportfolio.marketdata.insight.application.initializer.MarketHardCodingData;
import shop.shportfolio.marketdata.insight.application.mapper.MarketDataDtoMapper;
import shop.shportfolio.marketdata.insight.application.ports.input.MarketDataApplicationService;
import shop.shportfolio.marketdata.insight.application.ports.input.MarketDataTrackUseCase;
import shop.shportfolio.marketdata.insight.application.ports.output.bithumb.BithumbApiPort;
import shop.shportfolio.marketdata.insight.application.ports.output.repository.MarketItemRepositoryPort;
import shop.shportfolio.marketdata.insight.application.test.factory.*;
import shop.shportfolio.marketdata.insight.application.usecase.MarketDataTrackUseCaseImpl;
import shop.shportfolio.marketdata.insight.domain.entity.MarketItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class MarketDataApplicationServiceTest {

    @Mock
    private BithumbApiPort bithumbApiPort;
    @Mock
    private MarketItemRepositoryPort marketItemRepositoryPort;
    private MarketDataApplicationService marketDataApplicationService;
    private MarketDataDtoMapper mapper;
    private MarketDataTrackUseCase marketDataTrackUseCase;

    @BeforeEach
    public void setUp() {
        mapper = new MarketDataDtoMapper();
        marketDataTrackUseCase = new MarketDataTrackUseCaseImpl(bithumbApiPort, marketItemRepositoryPort);
        marketDataApplicationService = new MarketDataApplicationServiceImpl(marketDataTrackUseCase,mapper);
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
            MarketItem marketItem = mapper.marketItemBithumbDtoToMarketItem(dto);
            marketItems.add(marketItem);
        }
        Mockito.when(marketItemRepositoryPort.findAllMarketItems()).thenReturn(marketItems);
        // when
        List<MarketCodeTrackResponse> allMarkets = marketDataApplicationService.findAllMarkets();
        // then
        Assertions.assertNotNull(allMarkets);
        Assertions.assertFalse(allMarkets.isEmpty());
        System.out.println("allMarkets.size() = " + allMarkets.size());
        Assertions.assertEquals(mockMarketList.size(),allMarkets.size());
        Mockito.verify(marketItemRepositoryPort,Mockito.times(1)).findAllMarketItems();
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
}
