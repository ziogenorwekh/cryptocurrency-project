package shop.shportfolio.trading.application.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.command.track.request.*;
import shop.shportfolio.trading.application.dto.marketdata.candle.*;
import shop.shportfolio.trading.application.dto.marketdata.ticker.MarketTickerResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.trade.TradeTickResponseDto;
import shop.shportfolio.trading.application.handler.OrderBookManager;
import shop.shportfolio.trading.application.handler.track.MarketDataTrackHandler;
import shop.shportfolio.trading.application.handler.track.TradingTrackHandler;
import shop.shportfolio.trading.application.ports.input.TradingTrackUseCase;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.orderbook.MarketItem;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

import java.util.List;

@Component
public class TradingTrackUseCaseImpl implements TradingTrackUseCase {

    private final TradingTrackHandler tradingTrackHandler;
    private final OrderBookManager orderBookManager;
    private final MarketDataTrackHandler marketDataTrackHandler;

    @Autowired
    public TradingTrackUseCaseImpl(TradingTrackHandler tradingTrackHandler, OrderBookManager orderBookManager,
                                   MarketDataTrackHandler marketDataTrackHandler) {
        this.tradingTrackHandler = tradingTrackHandler;
        this.orderBookManager = orderBookManager;
        this.marketDataTrackHandler = marketDataTrackHandler;
    }

    @Override
    public OrderBook findOrderBook(OrderBookTrackQuery orderBookTrackQuery) {
        MarketItem item = marketDataTrackHandler.findMarketItemByMarketId(orderBookTrackQuery.getMarketId());
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
        return marketDataTrackHandler.findMarketItemByMarketId(marketTrackQuery.getMarketId());
    }

    @Override
    public List<MarketItem> findAllMarketItems() {
        return marketDataTrackHandler.findAllMarketItems();
    }

    @Override
    public List<CandleDayResponseDto> findCandleDayByMarket(CandleTrackQuery candleTrackQuery) {
        return marketDataTrackHandler.findCandleDayByMarketId(candleTrackQuery.getMarketId(),
                candleTrackQuery.getTo(), candleTrackQuery.getCount());
    }

    @Override
    public List<CandleWeekResponseDto> findCandleWeekByMarket(CandleTrackQuery candleTrackQuery) {
        return marketDataTrackHandler.findCandleWeekByMarketId(candleTrackQuery.getMarketId(),
                candleTrackQuery.getTo(), candleTrackQuery.getCount());
    }

    @Override
    public List<CandleMonthResponseDto> findCandleMonthByMarket(CandleTrackQuery candleTrackQuery) {
        return marketDataTrackHandler.findCandleMonthByMarketId(candleTrackQuery.getMarketId(),
                candleTrackQuery.getTo(), candleTrackQuery.getCount());
    }

    @Override
    public List<CandleMinuteResponseDto> findCandleMinuteByMarket(CandleMinuteTrackQuery candleMinuteTrackQuery) {
        return marketDataTrackHandler.findCandleMinuteByMarketId(candleMinuteTrackQuery.getUnit(),
                candleMinuteTrackQuery.getMarket(), candleMinuteTrackQuery.getTo(),
                candleMinuteTrackQuery.getCount());
    }

    @Override
    public MarketTickerResponseDto findMarketTickerByMarket(TickerTrackQuery tickerTrackQuery) {
        return marketDataTrackHandler.findMarketTickerByMarketId(tickerTrackQuery.getMarketId());
    }

    @Override
    public List<TradeTickResponseDto> findTradeTickByMarket(TradeTickTrackQuery trackQuery) {
        return marketDataTrackHandler.findTradeTickByMarketId(trackQuery.getMarketId(),trackQuery.getTo(),
                trackQuery.getCount(),
                trackQuery.getCursor(),trackQuery.getDaysAgo());
    }


}
