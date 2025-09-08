package shop.shportfolio.user.infrastructure.email.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

    private final MailConfigData mailConfigData;

    public MailConfig(MailConfigData mailConfigData) {
        this.mailConfigData = mailConfigData;
    }

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setJavaMailProperties(mailConfigData.getProperties());
        javaMailSender.setHost(mailConfigData.getHost());
        javaMailSender.setUsername(mailConfigData.getUsername());
        javaMailSender.setPassword(mailConfigData.getPassword());
        javaMailSender.setPort(mailConfigData.getPort());

        return javaMailSender;
    }
}
