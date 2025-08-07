package shop.shportfolio.portfolio.infrastructure.kafka.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.UserBalanceAvroModel;
import shop.shportfolio.common.kafka.listener.MessageHandler;
import shop.shportfolio.portfolio.application.dto.BalanceKafkaResponse;
import shop.shportfolio.portfolio.application.port.input.kafka.PortfolioBalanceKafkaListener;
import shop.shportfolio.portfolio.infrastructure.kafka.mapper.PortfolioMessageMapper;

import java.util.List;
@Component
public class PortfolioBalanceListener implements MessageHandler<UserBalanceAvroModel> {

    private final PortfolioMessageMapper portfolioMessageMapper;
    private final PortfolioBalanceKafkaListener portfolioBalanceKafkaListener;

    @Autowired
    public PortfolioBalanceListener(PortfolioMessageMapper portfolioMessageMapper,
                                    PortfolioBalanceKafkaListener portfolioBalanceKafkaListener) {
        this.portfolioMessageMapper = portfolioMessageMapper;
        this.portfolioBalanceKafkaListener = portfolioBalanceKafkaListener;
    }

    @Override
    public void handle(List<UserBalanceAvroModel> messaging, List<String> key) {
        messaging.forEach(userBalanceAvroModel -> {
            BalanceKafkaResponse balanceKafkaResponse = portfolioMessageMapper
                    .userBalanceAvroModelToBalanceKafkaResponse(userBalanceAvroModel);
            portfolioBalanceKafkaListener.handleCurrencyBalanceChange(balanceKafkaResponse);
        });
    }
}
