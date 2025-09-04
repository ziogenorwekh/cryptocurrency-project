package shop.shportfolio.matching.socket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.UUID;

@Slf4j
@Component
public class OrderBookWebSocketHandler extends TextWebSocketHandler {

    private final OrderBookSenderImpl orderBookSender;

    public OrderBookWebSocketHandler(OrderBookSenderImpl orderBookSender) {
        this.orderBookSender = orderBookSender;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String clientId = UUID.randomUUID().toString();
        orderBookSender.registerSession(clientId, session);
        log.info("Client {} connected", clientId);
        session.getAttributes().put("clientId", clientId);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 딱히 사용할 일 없음
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String clientId = (String) session.getAttributes().get("clientId");
        if (clientId != null) {
            orderBookSender.unregisterSession(clientId);
            log.info("Client {} disconnected", clientId);
        }
    }
}