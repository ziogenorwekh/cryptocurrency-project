package shop.shportfolio.trading.application.ports.input.socket;

import shop.shportfolio.trading.application.dto.marketdata.trade.TradeTickResponseDto;

public interface TradeListener {
    void onTradeReceived(TradeTickResponseDto dto);
}
