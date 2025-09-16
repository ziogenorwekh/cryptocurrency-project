package shop.shportfolio.trading.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import shop.shportfolio.trading.application.command.track.response.TradeTickTrackResponse;
import shop.shportfolio.trading.application.ports.output.socket.TradeSender;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class TradeSenderImpl implements TradeSender {

    private final Map<String, Set<WebSocketSession>> marketSubscribers = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> clientSessions = new ConcurrentHashMap<>();

    public void registerSession(String clientId, WebSocketSession session) {
        clientSessions.put(clientId, session);
    }

    public void unregisterSession(String clientId) {
        WebSocketSession session = clientSessions.remove(clientId);
        if (session != null) {
            marketSubscribers.values().forEach(sessions -> sessions.remove(session));
        }
    }

    public void updateClientMarkets(String clientId, Set<String> markets) {
        WebSocketSession session = clientSessions.get(clientId);
        if (session == null) return;

        // 이전 구독 제거
        marketSubscribers.values().forEach(sessions -> sessions.remove(session));

        // 새로운 구독 추가
        for (String market : markets) {
            marketSubscribers.computeIfAbsent(market, k -> ConcurrentHashMap.newKeySet()).add(session);
        }
    }

    @Override
    public void send(TradeTickTrackResponse response) {
        Set<WebSocketSession> sessions = marketSubscribers.get(response.getMarket());
        if (sessions != null) {
            TextMessage msg;
            try {
                msg = new TextMessage(new ObjectMapper().writeValueAsString(response));
            } catch (IOException e) {
                log.error("trade socket send error: {}", e.getMessage());
                return;
            }

            for (WebSocketSession session : sessions) {
                try {
                    if (session.isOpen()) session.sendMessage(msg);
                } catch (IOException e) {
                    log.error("trade socket send error: {}", e.getMessage());
                }
            }
        }
    }
}
