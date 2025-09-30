package shop.shportfolio.portfolio.application.port.input.kafka.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.common.domain.valueobject.DirectionType;
import shop.shportfolio.portfolio.application.dto.BalanceKafkaResponse;
import shop.shportfolio.portfolio.application.handler.PortfolioUpdateHandler;
import shop.shportfolio.portfolio.application.port.input.kafka.PortfolioBalanceListener;

@Slf4j
@Component
public class PortfolioBalanceListenerImpl implements PortfolioBalanceListener {

    private final PortfolioUpdateHandler portfolioUpdateHandler;

    @Autowired
    public PortfolioBalanceListenerImpl(PortfolioUpdateHandler portfolioUpdateHandler) {
        this.portfolioUpdateHandler = portfolioUpdateHandler;
    }

    @Override
    @Transactional
    public void handleCurrencyBalanceChange(BalanceKafkaResponse balanceKafkaResponse) {
        log.info("currency balance change received {}", balanceKafkaResponse);
        if (balanceKafkaResponse.getDirection() == DirectionType.ADD) {
            portfolioUpdateHandler.addMoney(balanceKafkaResponse);
        } else {
            portfolioUpdateHandler.subMoney(balanceKafkaResponse);
        }
    }

}
