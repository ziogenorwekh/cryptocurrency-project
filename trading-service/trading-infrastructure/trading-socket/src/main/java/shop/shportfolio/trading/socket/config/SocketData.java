package shop.shportfolio.trading.socket.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class SocketData {
    @Value("${socket.url.bithumb}")
    private String bithumbSocketUrl;
    @Value("${socket.url.my.server}")
    private String myServerUrl;
}
