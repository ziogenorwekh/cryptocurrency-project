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

}
