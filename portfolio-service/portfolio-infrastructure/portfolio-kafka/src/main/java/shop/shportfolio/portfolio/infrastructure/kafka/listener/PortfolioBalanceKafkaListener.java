package shop.shportfolio.portfolio.infrastructure.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.UserBalanceAvroModel;
import shop.shportfolio.common.kafka.listener.MessageHandler;
import shop.shportfolio.portfolio.application.dto.BalanceKafkaResponse;
import shop.shportfolio.portfolio.infrastructure.kafka.mapper.PortfolioMessageMapper;

import java.util.List;
@Slf4j
@Component
public class PortfolioBalanceKafkaListener implements MessageHandler<UserBalanceAvroModel> {

    private final PortfolioMessageMapper portfolioMessageMapper;
    private final shop.shportfolio.portfolio.application.port.input.kafka.PortfolioBalanceListener portfolioBalanceListener;

    @Autowired
    public PortfolioBalanceKafkaListener(PortfolioMessageMapper portfolioMessageMapper,
                                         shop.shportfolio.portfolio.application.port.input.kafka.PortfolioBalanceListener portfolioBalanceListener) {
        this.portfolioMessageMapper = portfolioMessageMapper;
        this.portfolioBalanceListener = portfolioBalanceListener;
    }

    @Override
    @KafkaListener(groupId = "portfolio-group", topics = "${kafka.userbalance.topic}")
    public void handle(List<UserBalanceAvroModel> messaging, List<String> key) {
        log.info("Received portfolio balance messages");
        messaging.forEach(userBalanceAvroModel -> {
            BalanceKafkaResponse balanceKafkaResponse = portfolioMessageMapper
                    .userBalanceAvroModelToBalanceKafkaResponse(userBalanceAvroModel);
            portfolioBalanceListener.handleCurrencyBalanceChange(balanceKafkaResponse);
        });
    }
}
