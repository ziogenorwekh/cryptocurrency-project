package shop.shportfolio.marketdata.insight.infrastructure.bithumb.client;

import lombok.extern.slf4j.Slf4j;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;

@Slf4j
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
//    @Override
//    public List<?> findCandles(String market, PeriodType period, Integer fetchCount) {
//        log.info("BithumbAPIClient findCandles market => {}, period => {}, fetchCount => {}",
//                market, period.name(), fetchCount);
//        switch (period) {
//            case THIRTY_MINUTES -> {
//                List<CandleMinuteResponseDto> candleMinuteResponseDtos = fetchCandles(market, "/candles/minutes/30", null, fetchCount, bithumbApiMapper::toCandleMinuteResponseDtoList);
//                log.info("[findCandles] first time => {}", candleMinuteResponseDtos.get(0).getCandleDateTimeKst());
//                log.info("[findCandles] last time => {}", candleMinuteResponseDtos.get(candleMinuteResponseDtos.size() - 1).getCandleDateTimeKst());
//                return candleMinuteResponseDtos;
//            }
//
//            case ONE_HOUR -> {
//                List<CandleMinuteResponseDto> candleMinuteResponseDtos = fetchCandles(market, "/candles/minutes/60", null, fetchCount, bithumbApiMapper::toCandleMinuteResponseDtoList);
//                log.info("[findCandles] first time => {}", candleMinuteResponseDtos.get(0).getCandleDateTimeKst());
//                log.info("[findCandles] last time => {}", candleMinuteResponseDtos.get(candleMinuteResponseDtos.size() - 1).getCandleDateTimeKst());
//                return candleMinuteResponseDtos;
//            }
//
//            case ONE_DAY -> {
//                List<CandleDayResponseDto> candleDayResponseDtos = fetchCandles(market, "/candles/days", null, fetchCount, bithumbApiMapper::toCandleDayResponseDtoList);
//                log.info("[findCandles] first time => {}", candleDayResponseDtos.get(0).getCandleDateTimeKst());
//                log.info("[findCandles] last time => {}", candleDayResponseDtos.get(candleDayResponseDtos.size() - 1).getCandleDateTimeKst());
//                return candleDayResponseDtos;
//            }
//            case ONE_WEEK -> {
//                List<CandleWeekResponseDto> list = fetchCandles(market, "/candles/weeks", null, fetchCount, bithumbApiMapper::toCandleWeekResponseDto);
//                log.info("[findCandles] first time => {}", list.get(0).getCandleDateTimeKst());
//                log.info("[findCandles] last time => {}", list.get(list.size() - 1).getCandleDateTimeKst());
//                return list;
//            }
//            case ONE_MONTH -> {
//                List<CandleMonthResponseDto> list = fetchCandles(market, "/candles/months", null, fetchCount, bithumbApiMapper::toCandleMonthResponseDtoList);
//                log.info("[findCandles] first time => {}", list.get(0).getCandleDateTimeKst());
//                log.info("[findCandles] last time => {}", list.get(list.size() - 1).getCandleDateTimeKst());
//                return list;
//            }
//            default -> {
//                throw new IllegalArgumentException("Unsupported PeriodType: " + period);
//            }
//        }
//    }


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
    private <T> List<T> fetchCandles(String market, String path, String to, Integer count,
                                     Function<String, List<T>> mapper) {

        int finalCount = (count == null || count <= 0) ? 190 : count;

        return webClient.get()
                .uri(uriBuilder -> {
                    UriBuilder builder = uriBuilder.path(path)
                            .queryParam("market", market)
                            .queryParam("count", finalCount);
                    if (to != null && !to.isEmpty()) builder = builder.queryParam("to", to);
                    return builder.build();
                })
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(webClientConfigData.getTimeout()))
                .map(mapper)
                .block();
    }
}
