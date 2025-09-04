package shop.shportfolio.matching.socket.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.socket")
public class SocketData {
    private String endpoint;
    private String path;
    private String bithumbSocketUri;
}
