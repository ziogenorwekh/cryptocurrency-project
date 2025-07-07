package shop.shportfolio.trading.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import shop.shportfolio.trading.application.command.create.*;
import shop.shportfolio.trading.application.command.track.LimitOrderTrackQuery;
import shop.shportfolio.trading.application.command.track.LimitOrderTrackResponse;
import shop.shportfolio.trading.application.command.track.OrderBookTrackQuery;
import shop.shportfolio.trading.application.command.track.OrderBookTrackResponse;
import shop.shportfolio.trading.application.mapper.TradingDataMapper;
import shop.shportfolio.trading.application.ports.input.*;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.OrderBook;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

@Slf4j
@Service
@Validated
public class TradingApplicationServiceImpl implements TradingApplicationService {

    private final TradingCreateOrderUseCase  createOrderUseCase;
    private final MarketOrderExecutionUseCase marketOrderExecutionUseCase;
    private final TradingTrackQueryUseCase tradingTrackQueryUseCase;
    private final TradingDataMapper tradingDataMapper;
    private final LimitOrderExecutionUseCase limitOrderExecutionUseCase;
    @Autowired
    public TradingApplicationServiceImpl(TradingCreateOrderUseCase createOrderUseCase,
                                         MarketOrderExecutionUseCase marketOrderExecutionUseCase,
                                         TradingTrackQueryUseCase tradingTrackQueryUseCase,
                                         TradingDataMapper tradingDataMapper,
                                         LimitOrderExecutionUseCase limitOrderExecutionUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.marketOrderExecutionUseCase = marketOrderExecutionUseCase;
        this.tradingTrackQueryUseCase = tradingTrackQueryUseCase;
        this.tradingDataMapper = tradingDataMapper;
        this.limitOrderExecutionUseCase = limitOrderExecutionUseCase;
    }

    @Override
    public CreateLimitOrderResponse createLimitOrder(CreateLimitOrderCommand createLimitOrderCommand) {
        LimitOrder limitOrder = createOrderUseCase.createLimitOrder(createLimitOrderCommand);
        limitOrderExecutionUseCase.executeLimitOrder(limitOrder);
        return tradingDataMapper.limitOrderToCreateLimitOrderResponse(limitOrder);
    }

    @Override
    public void createMarketOrder(CreateMarketOrderCommand createMarketOrderCommand) {
        MarketOrder marketOrder = createOrderUseCase.createMarketOrder(createMarketOrderCommand);
        marketOrderExecutionUseCase.executeMarketOrder(marketOrder);
    }

    @Override
    public CreateReservationResponse createReservationOrder(CreateReservationOrderCommand command) {
        ReservationOrder reservationOrder = createOrderUseCase.createReservationOrder(command);
        log.info("created Reservation Order ID: {} in Services", reservationOrder.getId().getValue());
        return tradingDataMapper.reservationOrderToCreateReservationResponse(reservationOrder);
    }

    @Override
    public OrderBookTrackResponse findOrderBook(OrderBookTrackQuery orderBookTrackQuery) {
        OrderBook orderBook = tradingTrackQueryUseCase.findOrderBook(orderBookTrackQuery);
        return tradingDataMapper.orderBookToOrderBookTrackResponse(orderBook);
    }

    @Override
    public LimitOrderTrackResponse findLimitOrderTrackByOrderId(LimitOrderTrackQuery limitOrderTrackQuery) {
        LimitOrder limitOrder = tradingTrackQueryUseCase.findLimitOrderByOrderId(limitOrderTrackQuery);
        return tradingDataMapper.limitOrderTrackToLimitOrderTrackResponse(limitOrder);
    }
}
