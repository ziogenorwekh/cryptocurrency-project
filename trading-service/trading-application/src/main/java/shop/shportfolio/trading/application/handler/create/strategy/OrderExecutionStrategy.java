package shop.shportfolio.trading.application.handler.create.strategy;

import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.event.TradingRecordedEvent;

import java.util.List;

@Deprecated
public interface OrderExecutionStrategy<T,V extends Order> {

    List<TradingRecordedEvent> execute(List<T> orderBookDto, V order);
}
