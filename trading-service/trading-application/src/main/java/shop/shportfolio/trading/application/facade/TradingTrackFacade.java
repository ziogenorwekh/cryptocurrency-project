package shop.shportfolio.trading.application.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.command.track.LimitOrderTrackQuery;
import shop.shportfolio.trading.application.command.track.MarketTrackQuery;
import shop.shportfolio.trading.application.command.track.OrderBookTrackQuery;
import shop.shportfolio.trading.application.command.track.ReservationOrderTrackQuery;
import shop.shportfolio.trading.application.handler.OrderBookManager;
import shop.shportfolio.trading.application.handler.track.TradingTrackHandler;
import shop.shportfolio.trading.application.ports.input.TradingTrackUseCase;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketItem;
import shop.shportfolio.trading.domain.entity.OrderBook;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

import java.util.List;

@Component
public class TradingTrackFacade implements TradingTrackUseCase {

    private final TradingTrackHandler tradingTrackHandler;
    private final OrderBookManager orderBookManager;

    @Autowired
    public TradingTrackFacade(TradingTrackHandler tradingTrackHandler, OrderBookManager orderBookManager) {
        this.tradingTrackHandler = tradingTrackHandler;
        this.orderBookManager = orderBookManager;
    }

    @Override
    public OrderBook findOrderBook(OrderBookTrackQuery orderBookTrackQuery) {
        MarketItem item = orderBookManager.findMarketItemById(orderBookTrackQuery.getMarketId());
        return orderBookManager.loadAdjustedOrderBook(orderBookTrackQuery.getMarketId(),
                item.getTickPrice().getValue());
    }

    @Override
    public LimitOrder findLimitOrderByOrderId(LimitOrderTrackQuery limitOrderTrackQuery) {
        return tradingTrackHandler.findLimitOrderByOrderIdAndUserId(limitOrderTrackQuery.getOrderId()
        ,limitOrderTrackQuery.getUserId());
    }

    @Override
    public ReservationOrder findReservationOrderByOrderIdAndUserId(ReservationOrderTrackQuery query) {
        return tradingTrackHandler
                .findReservationOrderByOrderIdAndUserId(query.getOrderId(), query.getUserId());
    }

    @Override
    public MarketItem findMarketItemByMarketItemId(MarketTrackQuery marketTrackQuery) {
        return tradingTrackHandler.findMarketItemByMarketId(marketTrackQuery.getMarketId());
    }

    @Override
    public List<MarketItem> findAllMarketItems() {
        return tradingTrackHandler.findAllMarketItems();
    }
}
