package shop.shportfolio.trading.socket.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import shop.shportfolio.trading.application.dto.marketdata.trade.TradeTickResponseDto;
import shop.shportfolio.trading.application.ports.input.socket.TradeListener;
import shop.shportfolio.trading.application.ports.output.socket.TradeSocketClient;
import shop.shportfolio.trading.socket.config.BuildSocketData;
import shop.shportfolio.trading.socket.config.SocketData;
import shop.shportfolio.trading.socket.mapper.BuildRequestJson;
import shop.shportfolio.trading.socket.mapper.TradingSocketDataMapper;

import java.net.URI;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class BithumbTradeSocketClient implements TradeSocketClient {

    private final SocketData socketData;
    private final Set<TradeListener> listeners = ConcurrentHashMap.newKeySet();
    private final BuildRequestJson buildRequestJson;
    private final TradingSocketDataMapper tradingSocketDataMapper;
    private Disposable sessionDisposable;

    @Autowired
    public BithumbTradeSocketClient(SocketData socketData, BuildRequestJson buildRequestJson,
                                    TradingSocketDataMapper tradingSocketDataMapper) {
        this.socketData = socketData;
        this.buildRequestJson = buildRequestJson;
        this.tradingSocketDataMapper = tradingSocketDataMapper;
    }


    @Override
    public void connect() {
        if (sessionDisposable != null && !sessionDisposable.isDisposed()) {
            log.warn("WebSocket already connected.");
            return;
        }

        ReactorNettyWebSocketClient client = new ReactorNettyWebSocketClient();
        URI uri = URI.create(socketData.getBithumbSocketUrl());

        sessionDisposable = client.execute(uri, session -> {
            String req = buildRequestJson.buildTrade();
            log.info("Sending orderbook subscription request: {}", req);

            return session.send(Mono.just(session.textMessage(req)))
                    .thenMany(session.receive().map(WebSocketMessage::getPayloadAsText)
                    .doOnNext(this::handleTradeMessage))
                    .then(Mono.never()); // 스트림 종료 방지
        }).subscribe();
    }

    @Override
    public void disconnect() {
        if (sessionDisposable != null && !sessionDisposable.isDisposed()) {
            sessionDisposable.dispose();
            log.info("WebSocket session disconnected.");
        } else {
            log.info("No active WebSocket session to disconnect.");
        }
    }

    @Override
    public void setTradeListener(TradeListener tradeListener) {
        listeners.add(tradeListener);
    }


    private void handleTradeMessage(String payload) {
        try {
            String payloadType = String.format("\"%s\":\"%s\"", BuildSocketData.type, BuildSocketData.trade);
            if (payload.contains(payloadType)) {
                TradeTickResponseDto dto = tradingSocketDataMapper.toTradeTickResponseDto(payload);
                listeners.forEach(listener -> listener.onTradeReceived(dto));
            } else {
                log.debug("Ignoring non-orderbook message: {}", payload);
            }
        } catch (Exception e) {
            log.error("Failed to parse message: {}", e.getMessage(), e);
            log.error("Payload: {}", payload);
        }
    }

}
