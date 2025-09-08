package shop.shportfolio.matching.socket.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import shop.shportfolio.matching.socket.OrderBookWebSocketHandler;


@Configuration
@EnableWebSocket
//@ComponentScan(basePackages = "shop.shportfolio.matching.socket")
@EnableConfigurationProperties(SocketData.class)
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final SocketData socketData;
    private final OrderBookWebSocketHandler orderBookWebSocketHandler;

    @Autowired
    public WebSocketConfiguration(SocketData socketData,
                                  OrderBookWebSocketHandler orderBookWebSocketHandler) {
        this.socketData = socketData;
        this.orderBookWebSocketHandler = orderBookWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(orderBookWebSocketHandler, socketData.getBithumbSocketUri())
                .setAllowedOrigins("*");
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }

}
