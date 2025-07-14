package shop.shportfolio.trading.application.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.command.track.request.*;
import shop.shportfolio.trading.application.dto.marketdata.candle.*;
import shop.shportfolio.trading.application.handler.OrderBookManager;
import shop.shportfolio.trading.application.handler.track.CandleTrackHandler;
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
    private final CandleTrackHandler candleTrackHandler;

    @Autowired
    public TradingTrackFacade(TradingTrackHandler tradingTrackHandler, OrderBookManager orderBookManager,
                              CandleTrackHandler candleTrackHandler) {
        this.tradingTrackHandler = tradingTrackHandler;
        this.orderBookManager = orderBookManager;
        this.candleTrackHandler = candleTrackHandler;
    }

    @Override
    public OrderBook findOrderBook(OrderBookTrackQuery orderBookTrackQuery) {
        MarketItem item = tradingTrackHandler.findMarketItemByMarketId(orderBookTrackQuery.getMarketId());
        return orderBookManager.loadAdjustedOrderBook(orderBookTrackQuery.getMarketId(),
                item.getTickPrice().getValue());
    }

    @Override
    public LimitOrder findLimitOrderByOrderId(LimitOrderTrackQuery limitOrderTrackQuery) {
        return tradingTrackHandler.findLimitOrderByOrderIdAndUserId(limitOrderTrackQuery.getOrderId()
                , limitOrderTrackQuery.getUserId());
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

    @Override
    public List<CandleDayResponseDto> findCandleDayByMarket(CandleTrackQuery candleTrackQuery) {
        return candleTrackHandler.findCandleDayByMarketId(candleTrackQuery.getMarketId(),
                candleTrackQuery.getTo(), candleTrackQuery.getCount());
    }

    @Override
    public List<CandleWeekResponseDto> findCandleWeekByMarket(CandleTrackQuery candleTrackQuery) {
        return candleTrackHandler.findCandleWeekByMarketId(candleTrackQuery.getMarketId(),
                candleTrackQuery.getTo(), candleTrackQuery.getCount());
    }

    @Override
    public List<CandleMonthResponseDto> findCandleMonthByMarket(CandleTrackQuery candleTrackQuery) {
        return candleTrackHandler.findCandleMonthByMarketId(candleTrackQuery.getMarketId(),
                candleTrackQuery.getTo(), candleTrackQuery.getCount());
    }

    @Override
    public List<CandleMinuteResponseDto> findCandleMinuteByMarket(CandleMinuteTrackQuery candleMinuteTrackQuery) {
        return candleTrackHandler.findCandleMinuteByMarketId(candleMinuteTrackQuery.getUnit(),
                candleMinuteTrackQuery.getMarket(), candleMinuteTrackQuery.getTo(),
                candleMinuteTrackQuery.getCount());
    }
}
