package shop.shportfolio.trading.application.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.command.track.LimitOrderTrackQuery;
import shop.shportfolio.trading.application.command.track.OrderBookTrackQuery;
import shop.shportfolio.trading.application.handler.OrderBookManager;
import shop.shportfolio.trading.application.handler.track.TradingTrackHandler;
import shop.shportfolio.trading.application.mapper.TradingDtoMapper;
import shop.shportfolio.trading.application.ports.input.TradingTrackQueryUseCase;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketItem;
import shop.shportfolio.trading.domain.entity.OrderBook;

@Component
public class TradingTrackQueryFacade implements TradingTrackQueryUseCase {

    private final TradingTrackHandler tradingTrackHandler;
    private final OrderBookManager orderBookManager;
    private final TradingDtoMapper tradingDtoMapper;

    @Autowired
    public TradingTrackQueryFacade(TradingTrackHandler tradingTrackHandler, OrderBookManager orderBookManager,
                                   TradingDtoMapper tradingDtoMapper) {
        this.tradingTrackHandler = tradingTrackHandler;
        this.orderBookManager = orderBookManager;
        this.tradingDtoMapper = tradingDtoMapper;
    }

    @Override
    public OrderBook findOrderBook(OrderBookTrackQuery orderBookTrackQuery) {
        MarketItem item = orderBookManager.findMarketItemById(orderBookTrackQuery.getMarketId());
        return orderBookManager.loadAdjustedOrderBook(orderBookTrackQuery.getMarketId(),
                item.getTickPrice().getValue());
    }

    @Override
    public LimitOrder findLimitOrderByOrderId(LimitOrderTrackQuery limitOrderTrackQuery) {
        return tradingTrackHandler.findLimitOrderByOrderId(limitOrderTrackQuery.getOrderId());
    }
}
