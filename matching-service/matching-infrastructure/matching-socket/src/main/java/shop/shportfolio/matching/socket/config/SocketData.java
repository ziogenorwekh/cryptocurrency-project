package shop.shportfolio.matching.socket.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Component
public class SocketData {
    @Value("${socket.url.bithumb}")
    private String bithumbSocketUrl;
    @Value("${socket.url.my.orderbook}")
    private String orderbookSocketUrl;
}
