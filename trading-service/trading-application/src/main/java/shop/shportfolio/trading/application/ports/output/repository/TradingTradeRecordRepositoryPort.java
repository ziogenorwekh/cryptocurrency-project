package shop.shportfolio.trading.application.ports.output.repository;

import shop.shportfolio.trading.domain.entity.Trade;

import java.util.List;

public interface TradingTradeRecordRepositoryPort {


    List<Trade> findTradesByMarketId(String marketId);
    void saveTrade(Trade trade);
}
