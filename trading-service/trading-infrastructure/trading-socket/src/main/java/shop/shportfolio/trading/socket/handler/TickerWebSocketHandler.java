package shop.shportfolio.trading.socket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import shop.shportfolio.trading.socket.TickerSenderImpl;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
public class TickerWebSocketHandler extends TextWebSocketHandler {

    private final TickerSenderImpl tickerSender;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    public TickerWebSocketHandler(TickerSenderImpl tickerSender) {
        this.tickerSender = tickerSender;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String clientId = UUID.randomUUID().toString();
        tickerSender.registerSession(clientId, session);
        log.info("Client {} connected", clientId);
        session.getAttributes().put("clientId", clientId);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, String> req = objectMapper.readValue(message.getPayload(), Map.class);
        if ("subscribe".equals(req.get("action"))) {
            String marketId = req.get("marketId");
            session.getAttributes().put("marketId", marketId);
            String clientId = (String) session.getAttributes().get("clientId");
            if (clientId != null) {
                tickerSender.updateClientMarkets(clientId, Set.of(marketId));
            }
            log.info("Client {} subscribed to {}", clientId, marketId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String clientId = (String) session.getAttributes().get("clientId");
        if (clientId != null) {
            tickerSender.unregisterSession(clientId);
            log.info("Client {} disconnected", clientId);
        }
    }
}
