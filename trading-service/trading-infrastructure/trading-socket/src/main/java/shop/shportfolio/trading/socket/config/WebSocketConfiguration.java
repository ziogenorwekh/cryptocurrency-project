package shop.shportfolio.trading.socket.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import shop.shportfolio.trading.socket.TickerAndTradeWebSocketHandler;


@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final SocketData socketData;
    private final TickerAndTradeWebSocketHandler tickerAndTradeWebSocketHandler;

    @Autowired
    public WebSocketConfiguration(SocketData socketData,
                                  TickerAndTradeWebSocketHandler tickerAndTradeWebSocketHandler) {
        this.socketData = socketData;
        this.tickerAndTradeWebSocketHandler = tickerAndTradeWebSocketHandler;
    }


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(tickerAndTradeWebSocketHandler, socketData.getMyServerUrl())
                .setAllowedOrigins("*");
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }
}
