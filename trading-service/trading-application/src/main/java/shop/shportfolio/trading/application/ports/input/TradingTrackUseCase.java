package shop.shportfolio.trading.application.ports.input;

import shop.shportfolio.trading.application.command.track.LimitOrderTrackQuery;
import shop.shportfolio.trading.application.command.track.MarketTrackQuery;
import shop.shportfolio.trading.application.command.track.OrderBookTrackQuery;
import shop.shportfolio.trading.application.command.track.ReservationOrderTrackQuery;
import shop.shportfolio.trading.application.dto.marketdata.CandleDayResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.CandleMonthResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.CandleWeekResponseDto;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketItem;
import shop.shportfolio.trading.domain.entity.OrderBook;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

import java.util.List;

public interface TradingTrackUseCase {

    OrderBook  findOrderBook(OrderBookTrackQuery orderBookTrackQuery);

    LimitOrder findLimitOrderByOrderId(LimitOrderTrackQuery limitOrderTrackQuery);

    ReservationOrder findReservationOrderByOrderIdAndUserId(ReservationOrderTrackQuery query);

    MarketItem findMarketItemByMarketItemId(MarketTrackQuery marketTrackQuery);

    List<MarketItem> findAllMarketItems();

    CandleDayResponseDto findCandleDayByMarketItemId(MarketTrackQuery marketTrackQuery);

    CandleWeekResponseDto findCandleWeekByMarketItemId(MarketTrackQuery marketTrackQuery);

    CandleMonthResponseDto findCandleMonthByMarketItemId(MarketTrackQuery marketTrackQuery);
}
