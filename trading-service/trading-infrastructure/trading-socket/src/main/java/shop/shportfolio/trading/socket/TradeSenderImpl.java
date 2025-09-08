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
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class TradeSenderImpl implements TradeSender {

    private final Map<String, WebSocketSession> clientSessions = new ConcurrentHashMap<>();

    // 클라이언트 세션 등록
    public void registerSession(String clientId, WebSocketSession session) {
        clientSessions.put(clientId, session);
    }

    public void unregisterSession(String clientId) {
        clientSessions.remove(clientId);
    }
    @Override
    public void send(TradeTickTrackResponse orderBookTrackResponse) {
        TextMessage msg;
        try {
            msg = new TextMessage(new ObjectMapper().writeValueAsString(orderBookTrackResponse));
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
