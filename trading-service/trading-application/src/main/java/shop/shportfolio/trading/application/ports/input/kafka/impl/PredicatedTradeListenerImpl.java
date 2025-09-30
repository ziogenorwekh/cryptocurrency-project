package shop.shportfolio.trading.application.ports.input.kafka.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.dto.context.TradeMatchingContext;
import shop.shportfolio.trading.application.dto.trade.PredicatedTradeKafkaResponse;
import shop.shportfolio.trading.application.handler.PredicatedTradeTransactionHandler;
import shop.shportfolio.trading.application.ports.input.kafka.PredicatedTradeListener;
import shop.shportfolio.trading.application.ports.output.kafka.TradePublisher;
import shop.shportfolio.trading.application.ports.output.kafka.UserBalancePublisher;

@Slf4j
@Component
public class PredicatedTradeListenerImpl implements PredicatedTradeListener {

    private final TradePublisher tradePublisher;
    private final UserBalancePublisher userBalancePublisher;
    private final PredicatedTradeTransactionHandler predicatedTradeTransactionHandler;

    public PredicatedTradeListenerImpl(TradePublisher tradePublisher,
                                       UserBalancePublisher userBalancePublisher,
                                       PredicatedTradeTransactionHandler predicatedTradeTransactionHandler) {
        this.tradePublisher = tradePublisher;
        this.userBalancePublisher = userBalancePublisher;
        this.predicatedTradeTransactionHandler = predicatedTradeTransactionHandler;
    }

    @Override
    public void process(PredicatedTradeKafkaResponse response) {
        if (!response.getBuyOrderId().contains("anonymous")) {
            predicatedTradeTransactionHandler.processTradeEvent(response, true)
                    .ifPresent(this::publishContext);
        }
        if (!response.getSellOrderId().contains("anonymous")) {
            predicatedTradeTransactionHandler.processTradeEvent(response, false)
                    .ifPresent(this::publishContext);
        }
    }

    private void publishContext(TradeMatchingContext context) {
        context.getUserBalanceUpdatedEvent()
                .ifPresent(userBalancePublisher::publish);
        context.getTradingRecordedEvents()
                .ifPresent(tradePublisher::publish);
    }

}
