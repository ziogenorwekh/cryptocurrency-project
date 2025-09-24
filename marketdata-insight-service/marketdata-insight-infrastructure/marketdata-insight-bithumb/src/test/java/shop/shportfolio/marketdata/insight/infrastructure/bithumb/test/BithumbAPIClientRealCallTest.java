package shop.shportfolio.marketdata.insight.infrastructure.bithumb.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleDayResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.request.CandleMinuteRequestDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleMinuteResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.request.CandleRequestDto;
import shop.shportfolio.marketdata.insight.application.dto.marketdata.MarketItemBithumbDto;
import shop.shportfolio.marketdata.insight.domain.valueobject.PeriodType;
import shop.shportfolio.marketdata.insight.infrastructure.bithumb.client.BithumbAPIClient;
import shop.shportfolio.marketdata.insight.infrastructure.bithumb.config.WebClientConfig;
import shop.shportfolio.marketdata.insight.infrastructure.bithumb.config.WebClientConfigData;
import shop.shportfolio.marketdata.insight.infrastructure.bithumb.mapper.BithumbApiMapper;
import shop.shportfolio.marketdata.insight.infrastructure.bithumb.test.config.TestConfig;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@SpringBootTest(classes = {BithumbAPIClient.class, WebClientConfig.class,
        BithumbApiMapper.class, TestConfig.class})
@EnableConfigurationProperties(WebClientConfigData.class)
public class BithumbAPIClientRealCallTest {

    @Autowired
    private BithumbAPIClient bithumbAPIClient;


    @Disabled("Manual test - run only when needed, excluded from regular test runs")
    @Test
    @DisplayName("마켓 아이템 조회 테스트")
    public void findMarketItemsTest() {
        // given && when
        List<MarketItemBithumbDto> marketItems = bithumbAPIClient.findMarketItems();
        // then
        Assertions.assertNotNull(marketItems);
        Assertions.assertTrue(marketItems.size() >= 100);
        Assertions.assertEquals("KRW-BTC", marketItems.get(0).getMarketId());
        Assertions.assertNotNull(marketItems.get(0).getEnglishName());
        Assertions.assertNotNull(marketItems.get(0).getKoreanName());
    }

    @Disabled("Manual test - run only when needed, excluded from regular test runs")
    @Test
    @DisplayName("분봉 테스트")
    public void findMinutesCandleTest() {
        // given
        CandleMinuteRequestDto requestDto = new CandleMinuteRequestDto(30,"KRW-BTC",null,10);
        // when
        List<CandleMinuteResponseDto> candleMinutes = bithumbAPIClient.findCandleMinutes(requestDto);
        // then
        for (CandleMinuteResponseDto candleMinute : candleMinutes) {
            System.out.println("candleMinute.toString() = " + candleMinute.toString());
        }
        Assertions.assertNotNull(candleMinutes);
        Assertions.assertEquals(10, candleMinutes.size());
        Assertions.assertEquals("KRW-BTC", candleMinutes.get(0).getMarketId());
    }
    @Disabled("Manual test - run only when needed, excluded from regular test runs")
    @Test
    @DisplayName("일봉 테스트")
    public void findDayCandleTest() {
        // given
        LocalDateTime lastAnalysisEndKst = LocalDateTime.now(ZoneOffset.ofHours(9)).withHour(0).withMinute(0).withSecond(0).withNano(0);
        CandleRequestDto requestDto = new CandleRequestDto("KRW-BTC",
                lastAnalysisEndKst.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),10);
        // when
        List<CandleDayResponseDto> candleDays = bithumbAPIClient.findCandleDays(requestDto);
        // then
        Assertions.assertNotNull(candleDays);
        Assertions.assertTrue(candleDays.size() >= 10);
        String candleDateTimeKst = candleDays.get(0).getCandleDateTimeKst();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(candleDateTimeKst, formatter);
        Assertions.assertTrue(localDateTime.isBefore(LocalDateTime.now()));
    }
//
//    @Disabled("Manual test - run only when needed, excluded from regular test runs")
//    @Test
//    @DisplayName("findCandles 최신 N개 조회 테스트")
//    public void findCandlesTest() {
//        // given
//        String market = "KRW-BTC";
//        int fetchCount = 5;
//
//        // when
//        List<?> candles = bithumbAPIClient.findCandles(market, PeriodType.THIRTY_MINUTES, fetchCount);
//
//        // then
//        Assertions.assertNotNull(candles);
//        Assertions.assertEquals(fetchCount, candles.size());
//        Object firstCandle = candles.get(0);
//        System.out.println("firstCandle = " + firstCandle);
//    }

}
