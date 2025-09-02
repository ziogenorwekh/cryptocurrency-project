package shop.shportfolio.portfolio.infrastructure.kafka.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.TradeAvroModel;
import shop.shportfolio.common.kafka.listener.MessageHandler;
import shop.shportfolio.portfolio.application.port.input.kafka.PortfolioTradeListener;
import shop.shportfolio.portfolio.infrastructure.kafka.mapper.PortfolioMessageMapper;

import java.util.List;

@Component
public class PortfolioTradeKafkaListener implements MessageHandler<TradeAvroModel> {
    
    private final PortfolioTradeListener portfolioTradeListener;
    private final PortfolioMessageMapper portfolioMessageMapper;

    public PortfolioTradeKafkaListener(PortfolioTradeListener portfolioTradeListener,
                                       PortfolioMessageMapper portfolioMessageMapper) {
        this.portfolioTradeListener = portfolioTradeListener;
        this.portfolioMessageMapper = portfolioMessageMapper;
    }

    @Override
    @KafkaListener(groupId = "portfolio-group", topics = "${kafka.trade.topic}")
    public void handle(List<TradeAvroModel> messaging, List<String> key) {
        messaging.forEach(trade -> {
            portfolioTradeListener.handleTrade(portfolioMessageMapper.tradeToTradeKafkaResponse(trade));
        });
    }
}
