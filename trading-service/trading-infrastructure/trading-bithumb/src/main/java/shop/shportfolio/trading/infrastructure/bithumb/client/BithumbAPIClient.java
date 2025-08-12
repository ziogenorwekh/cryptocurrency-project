package shop.shportfolio.trading.infrastructure.bithumb.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import shop.shportfolio.trading.application.dto.marketdata.MarketItemBithumbDto;
import shop.shportfolio.trading.application.dto.marketdata.candle.*;
import shop.shportfolio.trading.application.dto.marketdata.ticker.MarketTickerRequestDto;
import shop.shportfolio.trading.application.dto.marketdata.ticker.MarketTickerResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.trade.TradeTickRequestDto;
import shop.shportfolio.trading.application.dto.marketdata.trade.TradeTickResponseDto;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.trading.application.ports.output.marketdata.BithumbApiPort;
import shop.shportfolio.trading.infrastructure.bithumb.config.WebClientConfigData;
import shop.shportfolio.trading.infrastructure.bithumb.mapper.BithumbApiMapper;

import java.net.URI;
import java.time.Duration;
import java.util.List;

@Component
public class BithumbAPIClient implements BithumbApiPort {

    private final WebClient webClient;
    private final WebClientConfigData webClientConfigData;
    private final BithumbApiMapper bithumbApiMapper;

    @Autowired
    public BithumbAPIClient(WebClient webClient, WebClientConfigData webClientConfigData,
                            BithumbApiMapper bithumbApiMapper) {
        this.webClient = webClient;
        this.webClientConfigData = webClientConfigData;
        this.bithumbApiMapper = bithumbApiMapper;
    }

    @Override
    public OrderBookBithumbDto findOrderBookByMarketId(String marketId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/orderbook")
                        .queryParam("markets", marketId)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(webClientConfigData.getTimeout()))
                .map(bithumbApiMapper::toOrderBookBithumbDto).block();
    }

    @Override
    public List<MarketItemBithumbDto> findMarketItems() {
        return webClient.get().uri("/market/all")
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(webClientConfigData.getTimeout()))
                .map(bithumbApiMapper::toMarketItemBithumbDtoList).block();
    }

    @Override
    public List<CandleDayResponseDto> findCandleDays(CandleRequestDto requestDto) {
        return webClient.get().uri(uriBuilder -> {
                    UriBuilder builder = uriBuilder.path("/candles/days")
                            .queryParam("market", requestDto.getMarket());
                    return validateNullParams(requestDto.getTo(), requestDto.getCount(), builder);
                })
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(webClientConfigData.getTimeout()))
                .map(bithumbApiMapper::toCandleDayResponseDtoList)
                .block();
    }

    @Override
    public List<CandleWeekResponseDto> findCandleWeeks(CandleRequestDto requestDto) {
        return webClient.get().uri(uriBuilder -> {
                    UriBuilder builder = uriBuilder.path("/candles/weeks")
                            .queryParam("market", requestDto.getMarket());
                    return validateNullParams(requestDto.getTo(), requestDto.getCount(), builder);
                })
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(webClientConfigData.getTimeout()))
                .map(bithumbApiMapper::toCandleWeekResponseDto)
                .block();
    }

    @Override
    public List<CandleMonthResponseDto> findCandleMonths(CandleRequestDto requestDto) {
        return webClient.get().uri(uriBuilder -> {
                    UriBuilder builder = uriBuilder.path("/candles/months")
                            .queryParam("market", requestDto.getMarket());
                    return validateNullParams(requestDto.getTo(), requestDto.getCount(), builder);
                })
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(webClientConfigData.getTimeout()))
                .map(bithumbApiMapper::toCandleMonthResponseDtoList)
                .block();
    }

    @Override
    public List<CandleMinuteResponseDto> findCandleMinutes(CandleMinuteRequestDto requestDto) {
        return webClient.get().uri(uriBuilder -> {
                    UriBuilder builder = uriBuilder.path("/candles/minutes/" + requestDto.getUnit())
                            .queryParam("market", requestDto.getMarket());

                    return validateNullParams(requestDto.getTo(), requestDto.getCount(), builder);
                })
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(webClientConfigData.getTimeout()))
                .map(bithumbApiMapper::toCandleMinuteResponseDtoList).block();
    }

    @Override
    public MarketTickerResponseDto findTickerByMarketId(MarketTickerRequestDto marketTickerRequestDto) {
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path("/ticker")
                        .queryParam("markets", marketTickerRequestDto.getMarket())
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(webClientConfigData.getTimeout()))
                .map(bithumbApiMapper::toMarketTickerResponseDto).block();
    }

    @Override
    public List<TradeTickResponseDto> findTradeTicks(TradeTickRequestDto tradeTickRequestDto) {
        return webClient.get().uri(uriBuilder -> {
                    UriBuilder builder = uriBuilder.path("/trades/ticks")
                            .queryParam("market", tradeTickRequestDto.getMarket());
                    if (tradeTickRequestDto.getTo() != null && !tradeTickRequestDto.getTo().isEmpty()) {
                        builder = builder.queryParam("to", tradeTickRequestDto.getTo());
                    }
                    if (tradeTickRequestDto.getCount() != null) {
                        builder = builder.queryParam("count", tradeTickRequestDto.getCount());
                    }
                    if (tradeTickRequestDto.getCursor() != null && !tradeTickRequestDto.getCursor().isEmpty()) {
                        builder = builder.queryParam("cursor", tradeTickRequestDto.getCursor());
                    }
                    if (tradeTickRequestDto.getDaysAgo() != null) {
                        builder = builder.queryParam("daysAgo", tradeTickRequestDto.getDaysAgo());
                    }
                    return builder.build();
                })
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(webClientConfigData.getTimeout()))
                .map(bithumbApiMapper::toTradeTickResponseDtoList)
                .block();
    }

    private URI validateNullParams(String to, Integer count, UriBuilder builder) {
        if (to != null && !to.isEmpty()) {
            builder = builder.queryParam("to", to);
        }
        if (count != null) {
            builder = builder.queryParam("count", count);
        }
        return builder.build();
    }
}
