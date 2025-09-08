package shop.shportfolio.trading.application.ports.input.socket;


import shop.shportfolio.trading.application.dto.marketdata.ticker.MarketTickerResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.trade.TradeTickResponseDto;

public interface TickerListener {
    void onTickerReceived(MarketTickerResponseDto dto);
}
