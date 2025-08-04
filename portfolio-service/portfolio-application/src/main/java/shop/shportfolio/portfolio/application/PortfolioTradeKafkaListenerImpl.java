package shop.shportfolio.portfolio.application;

import org.springframework.stereotype.Component;
import shop.shportfolio.portfolio.application.dto.TradeKafkaResponse;
import shop.shportfolio.portfolio.application.port.input.kafka.PortfolioTradeKafkaListener;

@Component
public class PortfolioTradeKafkaListenerImpl implements PortfolioTradeKafkaListener {



    @Override
    public void handleBalanceChange(TradeKafkaResponse response) {

    }
}
