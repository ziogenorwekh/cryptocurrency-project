package shop.shportfolio.portfolio.application.port.input.kafka;

import shop.shportfolio.common.domain.valueobject.UserId;

public interface PortfolioUserListener {

    void createPortfolio(UserId userId);

    void deletePortfolio(UserId userId);
}
