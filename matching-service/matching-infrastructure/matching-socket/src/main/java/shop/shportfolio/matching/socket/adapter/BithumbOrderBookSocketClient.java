package shop.shportfolio.matching.socket.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.WebSocketMessage;
import shop.shportfolio.matching.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.matching.application.ports.input.socket.OrderBookListener;
import shop.shportfolio.matching.application.ports.output.socket.OrderBookSocketClient;
import shop.shportfolio.matching.socket.config.BuildSocketData;
import shop.shportfolio.matching.socket.config.SocketData;
import shop.shportfolio.matching.socket.mapper.BuildOrderBookRequestJson;
import shop.shportfolio.matching.socket.mapper.MatchingSocketDataMapper;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class BithumbOrderBookSocketClient implements OrderBookSocketClient {

    private final SocketData socketData;
    private final Set<OrderBookListener> listeners = ConcurrentHashMap.newKeySet();
    private final Set<String> subscribedMarkets = ConcurrentHashMap.newKeySet();
    private final BuildOrderBookRequestJson buildOrderBookRequestJson;
    private final MatchingSocketDataMapper matchingSocketDataMapper;

    private Disposable sessionDisposable;

    public BithumbOrderBookSocketClient(SocketData socketData,
                                        BuildOrderBookRequestJson buildOrderBookRequestJson,
                                        MatchingSocketDataMapper matchingSocketDataMapper) {
        this.socketData = socketData;
        this.buildOrderBookRequestJson = buildOrderBookRequestJson;
        this.matchingSocketDataMapper = matchingSocketDataMapper;
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
            String req = buildOrderBookRequestJson();
            log.info("Sending orderbook subscription request: {}", req);

            return session.send(Mono.just(session.textMessage(req)))
                    .thenMany(session.receive()
                            .map(WebSocketMessage::getPayloadAsText)
                            .doOnNext(this::handleMessage))
                    .then(Mono.never()); // 스트림 종료 방지
        }).subscribe();

        log.info("WebSocket session started.");
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
    public void subscribeMarket(String marketId) {
        subscribedMarkets.add(marketId);
        // 필요 시 여기서 서버에 재전송 로직 추가 가능
    }

    @Override
    public void setOrderBookListener(OrderBookListener listener) {
        listeners.add(listener);
    }

    private String buildOrderBookRequestJson() {
        return buildOrderBookRequestJson.buildOrderBook(1.0);
    }

    private void handleMessage(String payload) {
        try {
            String payloadType = String.format("\"%s\":\"%s\"", BuildSocketData.type, BuildSocketData.orderbook);
            if (payload.contains(payloadType)) {
                OrderBookBithumbDto dto = matchingSocketDataMapper.toOrderBookBithumbDto(payload);
                listeners.forEach(listener -> listener.onOrderBookReceived(dto));
            } else {
                log.debug("Ignoring non-orderbook message: {}", payload);
            }
        } catch (Exception e) {
            log.error("Failed to parse message: {}", e.getMessage(), e);
            log.error("Payload: {}", payload);
        }
    }
}
