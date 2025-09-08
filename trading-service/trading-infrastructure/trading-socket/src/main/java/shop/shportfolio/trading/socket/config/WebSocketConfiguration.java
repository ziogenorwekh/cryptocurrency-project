package shop.shportfolio.trading.socket.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import shop.shportfolio.trading.socket.handler.TickerWebSocketHandler;
import shop.shportfolio.trading.socket.handler.TradeWebSocketHandler;


@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final SocketData socketData;
    private final TickerWebSocketHandler tickerWebSocketHandler;
    private final TradeWebSocketHandler tradeWebSocketHandler;
    @Autowired
    public WebSocketConfiguration(SocketData socketData,
                                  TickerWebSocketHandler tickerWebSocketHandler,
                                  TradeWebSocketHandler tradeWebSocketHandler) {
        this.socketData = socketData;
        this.tickerWebSocketHandler = tickerWebSocketHandler;
        this.tradeWebSocketHandler = tradeWebSocketHandler;
    }


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(tickerWebSocketHandler, socketData.getTickerSocketUrl())
                .setAllowedOrigins("*");
        registry.addHandler(tradeWebSocketHandler, socketData.getTradeSocketUrl())
                .setAllowedOrigins("*");
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }
}
