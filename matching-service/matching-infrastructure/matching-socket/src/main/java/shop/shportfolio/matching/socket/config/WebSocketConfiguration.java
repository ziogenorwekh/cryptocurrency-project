package shop.shportfolio.matching.socket.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import shop.shportfolio.matching.application.ports.output.socket.OrderBookSender;
import shop.shportfolio.matching.socket.OrderBookSenderImpl;
import shop.shportfolio.matching.socket.OrderBookWebSocketHandler;

import java.util.UUID;

@Configuration
@EnableWebSocket
@EnableConfigurationProperties(SocketData.class)
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final SocketData socketData;
    private final OrderBookSenderImpl orderBookSender;
    private final OrderBookWebSocketHandler orderBookWebSocketHandler;

    @Autowired
    public WebSocketConfiguration(SocketData socketData, OrderBookSenderImpl orderBookSender,
                                  OrderBookWebSocketHandler orderBookWebSocketHandler) {
        this.socketData = socketData;
        this.orderBookSender = orderBookSender;
        this.orderBookWebSocketHandler = orderBookWebSocketHandler;
    }


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(orderBookWebSocketHandler, socketData.getPath())
                .setAllowedOrigins("*");
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }

}
