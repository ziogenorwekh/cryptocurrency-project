package shop.shportfolio.trading.application.ports.output.marketdata;

import shop.shportfolio.trading.application.dto.marketdata.*;
import shop.shportfolio.trading.application.dto.marketdata.candle.*;
import shop.shportfolio.trading.application.dto.marketdata.ticker.MarketTickerRequestDto;
import shop.shportfolio.trading.application.dto.marketdata.ticker.MarketTickerResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.trade.TradeTickRequestDto;
import shop.shportfolio.trading.application.dto.marketdata.trade.TradeTickResponseDto;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBithumbDto;

import java.util.List;

public interface BithumbApiPort {

    // 얘 직접 소비
    OrderBookBithumbDto findOrderBookByMarketId(String marketId);

    // 얘도 직접 소비
    List<MarketItemBithumbDto> findMarketItems();

    // 아래부터 마켓 & 분석 서비스로
    List<CandleDayResponseDto> findCandleDays(CandleRequestDto requestDto);

    List<CandleWeekResponseDto> findCandleWeeks(CandleRequestDto requestDto);

    List<CandleMonthResponseDto> findCandleMonths(CandleRequestDto requestDto);

    List<CandleMinuteResponseDto> findCandleMinutes(CandleMinuteRequestDto requestDto);

    MarketTickerResponseDto findTickerByMarketId(MarketTickerRequestDto marketTickerRequestDto);

    List<TradeTickResponseDto> findTradeTicks(TradeTickRequestDto tradeTickRequestDto);
}
