package shop.shportfolio.marketdata.insight.infrastructure.bithumb.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import shop.shportfolio.marketdata.insight.application.dto.candle.request.CandleMinuteRequestDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.request.CandleRequestDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleDayResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleMinuteResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleMonthResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleWeekResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.marketdata.MarketItemBithumbDto;
import shop.shportfolio.marketdata.insight.application.ports.output.bithumb.BithumbApiPort;
import shop.shportfolio.marketdata.insight.infrastructure.bithumb.config.WebClientConfigData;
import shop.shportfolio.marketdata.insight.infrastructure.bithumb.mapper.BithumbApiMapper;

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
