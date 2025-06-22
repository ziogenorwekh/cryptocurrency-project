package shop.shportfolio.user.email.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;


@Data
@Configuration
@ConfigurationProperties(prefix = "spring.mail")
public class MailConfigData {
    private String host;
    private int port;
    private String username;
    private String password;
    private Properties properties = new Properties();
}
