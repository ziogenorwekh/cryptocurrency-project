package shop.shportfolio.trading.application.ports.output.socket;

import shop.shportfolio.trading.application.command.track.response.TradeTickTrackResponse;

public interface TradeSender {

    void send(TradeTickTrackResponse orderBookTrackResponse);
}
