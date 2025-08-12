package shop.shportfolio.trading.infrastructure.bithumb.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import shop.shportfolio.trading.application.dto.marketdata.MarketItemBithumbDto;
import shop.shportfolio.trading.application.dto.marketdata.candle.*;
import shop.shportfolio.trading.application.dto.marketdata.ticker.MarketTickerRequestDto;
import shop.shportfolio.trading.application.dto.marketdata.ticker.MarketTickerResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.trade.TradeTickRequestDto;
import shop.shportfolio.trading.application.dto.marketdata.trade.TradeTickResponseDto;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.trading.application.ports.output.marketdata.BithumbApiPort;
import shop.shportfolio.trading.infrastructure.bithumb.config.URIConfigData;
import shop.shportfolio.trading.infrastructure.bithumb.mapper.BithumbApiMapper;

import java.time.Duration;
import java.util.List;

@Component
public class BithumbAPIClient implements BithumbApiPort {

    private final WebClient webClient;
    private final URIConfigData uriConfigData;
    private final BithumbApiMapper bithumbApiMapper;

    @Autowired
    public BithumbAPIClient(WebClient webClient, URIConfigData uriConfigData,
                            BithumbApiMapper bithumbApiMapper) {
        this.webClient = webClient;
        this.uriConfigData = uriConfigData;
        this.bithumbApiMapper = bithumbApiMapper;
    }

    @Override
    public OrderBookBithumbDto findOrderBookByMarketId(String marketId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(uriConfigData.getBithumbCommonURI() + "/orderbook")
                        .queryParam("markets", marketId)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(uriConfigData.getTimeout()))
                .map(bithumbApiMapper::toOrderBookBithumbDto).block();
    }

    @Override
    public List<MarketItemBithumbDto> findMarketItems() {
        return webClient.get().uri(uriConfigData.getBithumbCommonURI() + "/market/all")
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(uriConfigData.getTimeout()))
                .map(bithumbApiMapper::toMarketItemBithumbDtoList).block();
    }

    @Override
    public List<CandleDayResponseDto> findCandleDays(CandleRequestDto requestDto) {
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path(uriConfigData.getBithumbCommonURI() + "/candles/days")
                        .queryParam("market", requestDto.getMarket())
                        .queryParam("to", requestDto.getTo())
                        .queryParam("count", requestDto.getCount())
                        .build()).retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(uriConfigData.getTimeout()))
                .map(bithumbApiMapper::toCandleDayResponseDtoList).block();
    }

    @Override
    public List<CandleWeekResponseDto> findCandleWeeks(CandleRequestDto requestDto) {
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path(uriConfigData.getBithumbCommonURI() + "/candles/weeks")
                        .queryParam("market", requestDto.getMarket())
                        .queryParam("to", requestDto.getTo())
                        .queryParam("count", requestDto.getCount())
                        .build()).retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(uriConfigData.getTimeout()))
                .map(bithumbApiMapper::toCandleWeekResponseDto).block();
    }

    @Override
    public List<CandleMonthResponseDto> findCandleMonths(CandleRequestDto requestDto) {
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path(uriConfigData.getBithumbCommonURI() + "/candles/months")
                        .queryParam("market", requestDto.getMarket())
                        .queryParam("to", requestDto.getTo())
                        .queryParam("count", requestDto.getCount())
                        .build()).retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(uriConfigData.getTimeout()))
                .map(bithumbApiMapper::toCandleMonthResponseDtoList).block();
    }

    @Override
    public List<CandleMinuteResponseDto> findCandleMinutes(CandleMinuteRequestDto requestDto) {
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path(uriConfigData.getBithumbCommonURI() + "/candles/minutes/" + requestDto.getUnit())
                        .queryParam("market", requestDto.getMarket())
                        .queryParam("to", requestDto.getTo())
                        .queryParam("count", requestDto.getCount())
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(uriConfigData.getTimeout()))
                .map(bithumbApiMapper::toCandleMinuteResponseDtoList).block();
    }

    @Override
    public MarketTickerResponseDto findTickerByMarketId(MarketTickerRequestDto marketTickerRequestDto) {
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path(uriConfigData.getBithumbCommonURI() + "/ticker")
                        .queryParam("markets", marketTickerRequestDto.getMarket())
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(uriConfigData.getTimeout()))
                .map(bithumbApiMapper::toMarketTickerResponseDto).block();
    }

    @Override
    public List<TradeTickResponseDto> findTradeTicks(TradeTickRequestDto tradeTickRequestDto) {
        return webClient.get().uri(uriBuilder ->
                uriBuilder.path(uriConfigData.getBithumbCommonURI()+"/trades/ticks")
                        .queryParam("market",tradeTickRequestDto.getMarket())
                        .queryParam("to",tradeTickRequestDto.getTo())
                        .queryParam("count",tradeTickRequestDto.getCount())
                        .queryParam("cursor",tradeTickRequestDto.getCursor())
                        .queryParam("daysAgo",tradeTickRequestDto.getDaysAgo())
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(uriConfigData.getTimeout()))
                .map(bithumbApiMapper::toTradeTickResponseDtoList).block();
    }
}
