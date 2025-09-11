package shop.shportfolio.marketdata.insight.infrastructure.bithumb.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import shop.shportfolio.marketdata.insight.application.dto.candle.request.CandleMinuteRequestDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.request.CandleRequestDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.*;
import shop.shportfolio.marketdata.insight.application.dto.marketdata.MarketItemBithumbDto;
import shop.shportfolio.marketdata.insight.application.ports.output.bithumb.BithumbApiPort;
import shop.shportfolio.marketdata.insight.domain.valueobject.PeriodType;
import shop.shportfolio.marketdata.insight.infrastructure.bithumb.config.WebClientConfigData;
import shop.shportfolio.marketdata.insight.infrastructure.bithumb.mapper.BithumbApiMapper;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;

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


    // 일반 findCandles
    @Override
    public List<?> findCandles(String market, PeriodType period, int fetchCount) {
        switch (period) {
            case THIRTY_MINUTES -> {
                return fetchCandles(market, "/candles/minutes/30", null, fetchCount, bithumbApiMapper::toCandleMinuteResponseDtoList);
            }
            case ONE_HOUR -> {
                return fetchCandles(market, "/candles/minutes/60", null, fetchCount, bithumbApiMapper::toCandleMinuteResponseDtoList);
            }
            case ONE_DAY -> {
                return fetchCandles(market, "/candles/days", null, fetchCount, bithumbApiMapper::toCandleDayResponseDtoList);
            }
            case ONE_WEEK -> {
                return fetchCandles(market, "/candles/weeks", null, fetchCount, bithumbApiMapper::toCandleWeekResponseDto);
            }
            case ONE_MONTH -> {
                return fetchCandles(market, "/candles/months", null, fetchCount, bithumbApiMapper::toCandleMonthResponseDtoList);
            }
            default -> {
                throw new IllegalArgumentException("Unsupported PeriodType: " + period);
            }
        }
    }

    // findCandlesSince (LocalDateTime → String UTC 변환)
    @Override
    public List<?> findCandlesSince(String market, PeriodType periodType, LocalDateTime lastResult, int fetchCount) {
        String to = lastResult != null ? lastResult.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
        switch (periodType) {
            case THIRTY_MINUTES -> {
                return fetchCandles(market, "/candles/minutes/30", to, fetchCount, bithumbApiMapper::toCandleMinuteResponseDtoList);
            }
            case ONE_HOUR -> {
                return fetchCandles(market, "/candles/minutes/60", to, fetchCount, bithumbApiMapper::toCandleMinuteResponseDtoList);
            }
            case ONE_DAY -> {
                return fetchCandles(market, "/candles/days", to, fetchCount, bithumbApiMapper::toCandleDayResponseDtoList);
            }
            case ONE_WEEK -> {
                return fetchCandles(market, "/candles/weeks", to, fetchCount, bithumbApiMapper::toCandleWeekResponseDto);
            }
            case ONE_MONTH -> {
                return fetchCandles(market, "/candles/months", to, fetchCount, bithumbApiMapper::toCandleMonthResponseDtoList);
            }
            default -> {
                throw new IllegalArgumentException("Unsupported PeriodType: " + periodType);
            }
        }
    }

    @Override
    public List<MarketItemBithumbDto> findMarketItems() {
        return webClient.get().uri("/market/all")
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(webClientConfigData.getTimeout()))
                .map(bithumbApiMapper::toMarketItemBithumbDtoList).block();
    }

    // Candle*RequestDto 기반 메서드
    @Override
    public List<CandleMinuteResponseDto> findCandleMinutes(CandleMinuteRequestDto requestDto) {
        int unit = requestDto.getUnit();
        return fetchCandles(requestDto.getMarket(), "/candles/minutes/" + unit,
                requestDto.getTo(), requestDto.getCount(), bithumbApiMapper::toCandleMinuteResponseDtoList);
    }

    @Override
    public List<CandleDayResponseDto> findCandleDays(CandleRequestDto requestDto) {
        return fetchCandles(requestDto.getMarket(), "/candles/days",
                requestDto.getTo(), requestDto.getCount(), bithumbApiMapper::toCandleDayResponseDtoList);
    }

    @Override
    public List<CandleWeekResponseDto> findCandleWeeks(CandleRequestDto requestDto) {
        return fetchCandles(requestDto.getMarket(), "/candles/weeks",
                requestDto.getTo(), requestDto.getCount(), bithumbApiMapper::toCandleWeekResponseDto);
    }

    @Override
    public List<CandleMonthResponseDto> findCandleMonths(CandleRequestDto requestDto) {
        return fetchCandles(requestDto.getMarket(), "/candles/months",
                requestDto.getTo(), requestDto.getCount(), bithumbApiMapper::toCandleMonthResponseDtoList);
    }

    // 공통 fetch 메서드
    private <T> List<T> fetchCandles(String market, String path, String to, int count,
                                     Function<String, List<T>> mapper) {
        return webClient.get()
                .uri(uriBuilder -> {
                    UriBuilder builder = uriBuilder.path(path).queryParam("market", market);
                    if (to != null && !to.isEmpty()) builder = builder.queryParam("to", to);
                    if (count > 0) builder = builder.queryParam("count", count);
                    return builder.build();
                })
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(webClientConfigData.getTimeout()))
                .map(mapper)
                .block();
    }
}
