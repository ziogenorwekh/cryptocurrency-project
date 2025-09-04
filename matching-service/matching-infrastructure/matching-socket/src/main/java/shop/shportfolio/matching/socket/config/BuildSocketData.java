package shop.shportfolio.matching.socket.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
public class BuildSocketData {

    public static final String ticket = "ticket";
    public static final String type = "type";
    public static final String target = "target";
    public static final String orderbook = "orderbook";
    public static final String codes =  "codes";
    public static final String format = "format";
    public static final String defaultType = "DEFAULT";
}
