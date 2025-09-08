package shop.shportfolio.trading.application.ports.input.socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.command.track.response.TradeTickTrackResponse;
import shop.shportfolio.trading.application.dto.marketdata.trade.TradeTickResponseDto;
import shop.shportfolio.trading.application.mapper.TradingDataMapper;
import shop.shportfolio.trading.application.ports.output.socket.TradeSocketClient;
import shop.shportfolio.trading.application.ports.output.socket.TradeSender;

@Component
public class TradeManager implements TradeListener {

    private final TradeSocketClient tradeSocketClient;
    private final TradeSender tradeSender;
    private final TradingDataMapper tradingDataMapper;

    @Autowired
    public TradeManager(TradeSocketClient tradeSocketClient, TradeSender tradeSender,
                        TradingDataMapper tradingDataMapper) {
        this.tradeSocketClient = tradeSocketClient;
        this.tradeSender = tradeSender;
        this.tradingDataMapper = tradingDataMapper;
        tradeSocketClient.connect();
        tradeSocketClient.setTradeListener(this);
    }

    @Override
    public void onTradeReceived(TradeTickResponseDto dto) {
        TradeTickTrackResponse tradeTickTrackResponse = tradingDataMapper.tradeTickResponseDtoToTradeTickResponse(dto);
        tradeSender.send(tradeTickTrackResponse);
    }
}
