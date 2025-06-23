package shop.shportfolio.trading.application.ports.output.repository;

import org.springframework.stereotype.Repository;
import shop.shportfolio.trading.domain.entity.LimitOrder;

@Repository
public interface TradingRepositoryAdapter {

    LimitOrder save(LimitOrder limitOrder);
}
