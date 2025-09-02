package shop.shportfolio.portfolio.application.port.input.kafka;

import shop.shportfolio.common.domain.valueobject.UserId;

public interface PortfolioUserKafkaListener {

    void deletePortfolio(UserId userId);
}
