package shop.shportfolio.trading.infrastructure.bithumb.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import shop.shportfolio.trading.application.dto.marketdata.MarketItemBithumbDto;
import shop.shportfolio.trading.application.dto.marketdata.candle.CandleDayResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.candle.CandleMinuteRequestDto;
import shop.shportfolio.trading.application.dto.marketdata.candle.CandleMinuteResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.candle.CandleRequestDto;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.trading.infrastructure.bithumb.client.BithumbAPIClient;
import shop.shportfolio.trading.infrastructure.bithumb.config.WebClientConfig;
import shop.shportfolio.trading.infrastructure.bithumb.config.WebClientConfigData;
import shop.shportfolio.trading.infrastructure.bithumb.mapper.BithumbApiMapper;
import shop.shportfolio.trading.infrastructure.bithumb.test.config.TestConfig;

import java.time.LocalDateTime;
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
    @DisplayName("빗썸 API 호가창 조회 테스트")
    public void findOrderBookTest() {
        // given
        String marketId = "KRW-BTC";
        Integer minimumSize = 5;
        // when
        OrderBookBithumbDto bookBithumbDto = bithumbAPIClient.findOrderBookByMarketId(marketId);
        // then
        Assertions.assertNotNull(bookBithumbDto);
        Assertions.assertTrue(bookBithumbDto.getAsks().size() >= minimumSize);
        Assertions.assertTrue(bookBithumbDto.getBids().size() >= minimumSize);
        Assertions.assertNotNull(bookBithumbDto.getTotalAskSize());
    }

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
        CandleMinuteRequestDto requestDto = new CandleMinuteRequestDto(5,"KRW-BTC",null,10);
        // when
        List<CandleMinuteResponseDto> candleMinutes = bithumbAPIClient.findCandleMinutes(requestDto);
        // then
        Assertions.assertNotNull(candleMinutes);
        Assertions.assertEquals(10, candleMinutes.size());
        Assertions.assertEquals("KRW-BTC", candleMinutes.get(0).getMarketId());
    }
    @Disabled("Manual test - run only when needed, excluded from regular test runs")
    @Test
    @DisplayName("일봉 테스트")
    public void findDayCandleTest() {
        // given
        CandleRequestDto requestDto = new CandleRequestDto("KRW-BTC",null,10);
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
}
