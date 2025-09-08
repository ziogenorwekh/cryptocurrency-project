package shop.shportfolio.trading.application.ports.input.socket;

import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.command.track.response.TickerTrackResponse;
import shop.shportfolio.trading.application.dto.marketdata.ticker.MarketTickerResponseDto;
import shop.shportfolio.trading.application.mapper.TradingDataMapper;
import shop.shportfolio.trading.application.ports.output.socket.TickerSocketClient;
import shop.shportfolio.trading.application.ports.output.socket.TickerSender;

@Component
public class TickerManager implements TickerListener {

    private final TickerSocketClient tickerSocketClient;
    private final TickerSender tickerSender;
    private final TradingDataMapper tradingDataMapper;

    public TickerManager(TickerSocketClient tickerSocketClient, TickerSender tickerSender,
                         TradingDataMapper tradingDataMapper) {
        this.tickerSocketClient = tickerSocketClient;
        this.tickerSender = tickerSender;
        this.tradingDataMapper = tradingDataMapper;
        tickerSocketClient.connect();
        tickerSocketClient.setTickerListener(this);
    }

    @Override
    public void onTickerReceived(MarketTickerResponseDto dto) {
        TickerTrackResponse tickerTrackResponse = tradingDataMapper.marketTickerResponseDtoToTickerTrackResponse(dto);
        tickerSender.send(tickerTrackResponse);
    }
}
