package shop.shportfolio.marketdata.insight.application.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.marketdata.insight.application.command.response.*;
import shop.shportfolio.marketdata.insight.application.dto.candle.request.CandleMinuteRequestDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.request.CandleRequestDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleDayResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleMinuteResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleMonthResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleWeekResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.marketdata.MarketItemBithumbDto;
import shop.shportfolio.marketdata.insight.domain.entity.AIAnalysisResult;
import shop.shportfolio.marketdata.insight.domain.entity.MarketItem;


@Component
public class MarketDataDtoMapper {

    public AiAnalysisTrackResponse aiAnalysisResultToAiAnalysisTrackResponse(AIAnalysisResult aiAnalysisResult) {
        return AiAnalysisTrackResponse.builder()
                .marketId(aiAnalysisResult.getMarketId().getValue())
                .analysisTime(aiAnalysisResult.getAnalysisTime().getValue().toLocalDateTime())
                .momentumScore(aiAnalysisResult.getMomentumScore().getValue())
                .periodEnd(aiAnalysisResult.getPeriodEnd().getValue().toLocalDateTime())
                .periodStart(aiAnalysisResult.getPeriodStart().getValue().toLocalDateTime())
                .periodType(aiAnalysisResult.getPeriodType())
                .priceTrend(aiAnalysisResult.getPriceTrend())
                .signal(aiAnalysisResult.getSignal())
                .summaryCommentEng(aiAnalysisResult.getSummaryCommentEng().getValue())
                .summaryCommentKor(aiAnalysisResult.getSummaryCommentKor().getValue())
                .build();
    }


    public MarketItem marketItemBithumbDtoToMarketItem(MarketItemBithumbDto marketItemBithumbDto) {
        return MarketItem.builder()
                .marketId(new MarketId(marketItemBithumbDto.getMarketId()))
                .marketKoreanName(new MarketKoreanName(marketItemBithumbDto.getKoreanName()))
                .marketEnglishName(new MarketEnglishName(marketItemBithumbDto.getEnglishName()))
                .marketStatus(MarketStatus.ACTIVE)
                .build();
    }

    public CandleMinuteRequestDto toCandleRequestMinuteDto(Integer unit, String marketId, String to, Integer count) {
        return CandleMinuteRequestDto.builder()
                .unit(unit)
                .market(marketId)
                .to(to)
                .count(count)
                .build();
    }

    public CandleRequestDto toCandleRequestDto(String marketId, String to, Integer count) {
        return CandleRequestDto.builder()
                .market(marketId)
                .to(to)
                .count(count)
                .build();
    }


    public MarketCodeTrackResponse marketItemToMarketItemTrackResponse(MarketItem marketItem) {
        return MarketCodeTrackResponse.builder()
                .marketId(marketItem.getId().getValue())
                .marketEnglishName(marketItem.getMarketEnglishName().getValue())
                .marketKoreanName(marketItem.getMarketKoreanName().getValue())
                .build();
    }

    public CandleMinuteTrackResponse candleMinuteResponseDtoToCandleMinuteTrackResponse(CandleMinuteResponseDto dto) {
        return new CandleMinuteTrackResponse(
                dto.getMarketId(),
                dto.getCandleDateTimeKST(),
                dto.getOpeningPrice(),
                dto.getHighPrice(),
                dto.getLowPrice(),
                dto.getTradePrice(),
                dto.getTimestamp(),
                dto.getCandleAccTradePrice(),
                dto.getCandleAccTradeVolume(),
                dto.getUnit()
        );
    }

    public CandleDayTrackResponse candleDayResponseDtoToCandleDayTrackResponse(CandleDayResponseDto dto) {
        return new CandleDayTrackResponse(
                dto.getMarket(),
                dto.getCandleDateTimeUtc(),
                dto.getCandleDateTimeKst(),
                dto.getOpeningPrice(),
                dto.getHighPrice(),
                dto.getLowPrice(),
                dto.getTradePrice(),
                dto.getCandleAccTradePrice(),
                dto.getCandleAccTradeVolume(),
                dto.getPrevClosingPrice(),
                dto.getChangePrice(),
                dto.getChangeRate()
        );
    }

    public CandleWeekTrackResponse candleWeekResponseDtoToCandleWeekTrackResponse(CandleWeekResponseDto dto) {
        return new CandleWeekTrackResponse(
                dto.getMarket(),
                dto.getCandleDateTimeUtc(),
                dto.getCandleDateTimeKst(),
                dto.getOpeningPrice(),
                dto.getHighPrice(),
                dto.getLowPrice(),
                dto.getTradePrice(),
                dto.getTimestamp(),
                dto.getCandleAccTradePrice(),
                dto.getCandleAccTradeVolume(),
                dto.getFirstDayOfPeriod()
        );
    }


    public CandleMonthTrackResponse candleMonthResponseDtoToCandleMonthTrackResponse(CandleMonthResponseDto dto) {
        return new CandleMonthTrackResponse(
                dto.getMarket(),
                dto.getCandleDateTimeUtc(),
                dto.getCandleDateTimeKst(),
                dto.getOpeningPrice(),
                dto.getHighPrice(),
                dto.getLowPrice(),
                dto.getTradePrice(),
                dto.getTimestamp(),
                dto.getCandleAccTradePrice(),
                dto.getCandleAccTradeVolume(),
                dto.getFirstDayOfPeriod()
        );
    }
}
