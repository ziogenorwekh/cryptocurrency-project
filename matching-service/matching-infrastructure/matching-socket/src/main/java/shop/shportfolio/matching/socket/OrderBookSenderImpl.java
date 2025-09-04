package shop.shportfolio.matching.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import shop.shportfolio.matching.application.command.OrderBookTrackResponse;
import shop.shportfolio.matching.application.ports.output.socket.OrderBookSender;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class OrderBookSenderImpl implements OrderBookSender {

    private final Map<String, WebSocketSession> clientSessions = new ConcurrentHashMap<>();

    // 클라이언트 세션 등록
    public void registerSession(String clientId, WebSocketSession session) {
        clientSessions.put(clientId, session);
    }

    public void unregisterSession(String clientId) {
        clientSessions.remove(clientId);
    }

    // 마켓별 OrderBook 전송
    @Override
    public void send(OrderBookTrackResponse response) {
        TextMessage msg;
        try {
            msg = new TextMessage(new ObjectMapper().writeValueAsString(response));
        } catch (IOException e) {
            log.error("socket send error is : {}", e.getMessage());
            return;
        }

        clientSessions.values().forEach(sess -> {
            try {
                if (sess.isOpen()) sess.sendMessage(msg);
            } catch (IOException e) {
                log.error("socket send error is : {}", e.getMessage());
            }
        });
    }
}