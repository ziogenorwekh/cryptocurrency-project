package shop.shportfolio.matching.socket.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.WebSocketMessage;
import shop.shportfolio.matching.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.matching.application.ports.input.socket.OrderBookListener;
import shop.shportfolio.matching.application.ports.output.socket.BithumbSocketClient;
import shop.shportfolio.matching.socket.config.SocketData;
import shop.shportfolio.matching.socket.mapper.BuildOrderBookRequestJson;

import java.net.URI;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class BithumbSocketClientAdapter implements BithumbSocketClient {

    private final SocketData socketData;
    private final Set<OrderBookListener> listeners = ConcurrentHashMap.newKeySet();
    private final ObjectMapper mapper;
    private final Set<String> subscribedMarkets = ConcurrentHashMap.newKeySet();
    private final BuildOrderBookRequestJson buildOrderBookRequestJson;

    public BithumbSocketClientAdapter(SocketData socketData, ObjectMapper mapper,
                                      BuildOrderBookRequestJson buildOrderBookRequestJson) {
        this.socketData = socketData;
        this.mapper = mapper.copy();
        this.buildOrderBookRequestJson = buildOrderBookRequestJson;
    }

    @Override
    public void connect() {
        ReactorNettyWebSocketClient client = new ReactorNettyWebSocketClient();
        URI uri = URI.create(socketData.getBithumbSocketUri());

        client.execute(uri, session -> {
            // JSON 구독 요청
            String req = buildTickerRequestJson();
            log.info("Sending ticker request: {}", req);

            return session.send(Mono.just(session.textMessage(req)))
                    .thenMany(session.receive()
                            .map(WebSocketMessage::getPayloadAsText)
                            .doOnNext(this::handleMessage)
                    )
                    .then();
        }).block(Duration.ofSeconds(30)); // 연결 최대 30초 유지
    }

    @Override
    public void subscribeMarket(String marketId) {
        subscribedMarkets.add(marketId);
    }

    @Override
    public void disconnect() {
        // ReactorNetty는 연결 종료 시 session.dispose() 처리 가능
        log.info("Reactive WebSocket disconnect called. 서버에 따라 자동 종료될 수 있음.");
    }

    @Override
    public void setOrderBookListener(OrderBookListener listener) {
        listeners.add(listener);
    }

    private String buildTickerRequestJson() {
        try {
            String request = buildOrderBookRequestJson.buildOrderBook();
            return mapper.writeValueAsString(request);
        } catch (Exception e) {
            throw new RuntimeException("Failed to build ticker request JSON", e);
        }
    }

    private void handleMessage(String payload) {
        log.info("Raw message received: {}", payload);
        try {
            // ticker 메시지만 처리
            if (payload.contains("\"type\":\"ticker\"")) {
                OrderBookBithumbDto dto = mapper.readValue(payload, OrderBookBithumbDto.class);

                listeners.forEach(listener -> listener.onOrderBookReceived(dto));
            } else {
                log.debug("Ignoring non-ticker message: {}", payload);
            }
        } catch (Exception e) {
            log.error("Failed to parse message: {}", e.getMessage());
            log.error("Payload: {}", payload);
        }
    }
}
