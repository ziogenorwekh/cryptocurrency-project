package shop.shportfolio.trading.application.ports.output.socket;

import shop.shportfolio.trading.application.command.track.response.TickerTrackResponse;

public interface TickerSender {

    void sendAll(TickerTrackResponse tickerTrackResponse);
    void sendToClient(TickerTrackResponse tickerTrackResponse); // 단건 전송
}
