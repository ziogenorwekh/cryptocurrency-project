package shop.shportfolio.trading.application.ports.output.socket;

import shop.shportfolio.trading.application.command.track.response.TickerTrackResponse;
import shop.shportfolio.trading.application.command.track.response.TradeTickTrackResponse;

public interface TickerSender {

    void send(TickerTrackResponse tickerTrackResponse);
}
