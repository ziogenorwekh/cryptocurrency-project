package shop.shportfolio.trading.application.ports.output.repository;

import shop.shportfolio.trading.domain.entity.Trade;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TradingTradeRecordRepositoryPort {


    List<Trade> findTradesByMarketId(String marketId);
    void saveTrade(Trade trade);

    Optional<Trade> findTopByMarketIdOrderByCreatedAtDesc(String marketId);

    List<Trade> findTradesByMarketIdAndCreatedAtBetween(String marketId, LocalDateTime from,
                                                        LocalDateTime to, Integer count);
}
