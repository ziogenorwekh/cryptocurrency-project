package shop.shportfolio.portfolio.infrastructure.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.UserIdAvroModel;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.common.kafka.listener.MessageHandler;
import shop.shportfolio.portfolio.application.port.input.kafka.PortfolioUserListener;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class PortfolioUserKafkaListener implements MessageHandler<UserIdAvroModel> {

    private final PortfolioUserListener portfolioUserListener;

    public PortfolioUserKafkaListener(PortfolioUserListener portfolioUserListener) {
        this.portfolioUserListener = portfolioUserListener;
    }
    
    @Override
    @KafkaListener(groupId = "portfolio-group", topics = "${kafka.user.topic}")
    public void handle(List<UserIdAvroModel> messaging, List<String> key) {
        log.info("Received portfolio balance messages");
        messaging.forEach(userIdAvroModel -> {
            switch (userIdAvroModel.getMessageType()) {
                case CREATE -> portfolioUserListener.createPortfolio(new UserId(
                        UUID.fromString(userIdAvroModel.getUserId())));
                case DELETE -> portfolioUserListener.deletePortfolio(new UserId(
                        UUID.fromString(userIdAvroModel.getUserId())));
            }
        });
    }
}
