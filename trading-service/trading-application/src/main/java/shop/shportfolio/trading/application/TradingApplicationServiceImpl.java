package shop.shportfolio.trading.application;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import shop.shportfolio.trading.application.command.create.*;
import shop.shportfolio.trading.application.command.track.request.LimitOrderTrackQuery;
import shop.shportfolio.trading.application.command.track.request.OrderBookTrackQuery;
import shop.shportfolio.trading.application.command.track.request.ReservationOrderTrackQuery;
import shop.shportfolio.trading.application.command.track.response.LimitOrderTrackResponse;
import shop.shportfolio.trading.application.command.track.response.OrderBookTrackResponse;
import shop.shportfolio.trading.application.command.track.response.ReservationOrderTrackResponse;
import shop.shportfolio.trading.application.command.update.CancelLimitOrderCommand;
import shop.shportfolio.trading.application.command.update.CancelOrderResponse;
import shop.shportfolio.trading.application.command.update.CancelReservationOrderCommand;
import shop.shportfolio.trading.application.mapper.TradingDataMapper;
import shop.shportfolio.trading.application.ports.input.*;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;

@Slf4j
@Service
@Validated
public class TradingApplicationServiceImpl implements TradingApplicationService {

    private final TradingCreateOrderUseCase createOrderUseCase;
    private final TradingTrackUseCase tradingTrackUseCase;
    private final TradingDataMapper tradingDataMapper;
    private final TradingUpdateUseCase tradingUpdateUseCase;
    private final ExecuteOrderMatchingUseCase executeOrderMatchingUseCase;
    @Autowired
    public TradingApplicationServiceImpl(TradingCreateOrderUseCase createOrderUseCase,
                                         TradingTrackUseCase tradingTrackUseCase,
                                         TradingDataMapper tradingDataMapper,
                                         TradingUpdateUseCase tradingUpdateUseCase,
                                         ExecuteOrderMatchingUseCase executeOrderMatchingUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.tradingTrackUseCase = tradingTrackUseCase;
        this.tradingDataMapper = tradingDataMapper;
        this.tradingUpdateUseCase = tradingUpdateUseCase;
        this.executeOrderMatchingUseCase = executeOrderMatchingUseCase;
    }

    @Override
    public CreateLimitOrderResponse createLimitOrder(@Valid CreateLimitOrderCommand createLimitOrderCommand) {
        LimitOrder limitOrder = createOrderUseCase.createLimitOrder(createLimitOrderCommand);
//        executeOrderMatchingUseCase.executeLimitOrder(limitOrder);
        return tradingDataMapper.limitOrderToCreateLimitOrderResponse(limitOrder);
    }

    @Override
    public void createMarketOrder(@Valid CreateMarketOrderCommand createMarketOrderCommand) {
        MarketOrder marketOrder = createOrderUseCase.createMarketOrder(createMarketOrderCommand);
//        executeOrderMatchingUseCase.executeMarketOrder(marketOrder);
    }

    @Override
    public CreateReservationResponse createReservationOrder(@Valid CreateReservationOrderCommand command) {
        ReservationOrder reservationOrder = createOrderUseCase.createReservationOrder(command);
        log.info("created Reservation Order ID: {} in Services", reservationOrder.getId().getValue());
        return tradingDataMapper.reservationOrderToCreateReservationResponse(reservationOrder);
    }

    @Override
    public OrderBookTrackResponse findOrderBook(@Valid OrderBookTrackQuery orderBookTrackQuery) {
        OrderBook orderBook = tradingTrackUseCase.findOrderBook(orderBookTrackQuery);
        return tradingDataMapper.orderBookToOrderBookTrackResponse(orderBook);
    }

    @Override
    public LimitOrderTrackResponse findLimitOrderTrackByOrderIdAndUserId(@Valid LimitOrderTrackQuery limitOrderTrackQuery) {
        LimitOrder limitOrder = tradingTrackUseCase.findLimitOrderByOrderId(limitOrderTrackQuery);
        return tradingDataMapper.limitOrderTrackToLimitOrderTrackResponse(limitOrder);
    }

    @Override
    public ReservationOrderTrackResponse
    findReservationOrderTrackByOrderIdAndUserId(@Valid ReservationOrderTrackQuery query) {
        ReservationOrder order = tradingTrackUseCase.findReservationOrderByOrderIdAndUserId(query);
        return tradingDataMapper.reservationOrderToReservationOrderTrackResponse(order);
    }

    @Override
    public CancelOrderResponse cancelLimitOrder(@Valid CancelLimitOrderCommand cancelLimitOrderCommand) {
        LimitOrder limitOrder = tradingUpdateUseCase.cancelLimitOrder(cancelLimitOrderCommand);
        return tradingDataMapper.limitOrderToCancelOrderResponse(limitOrder);
    }

    @Override
    public CancelOrderResponse cancelReservationOrder(@Valid CancelReservationOrderCommand command) {
        ReservationOrder reservationOrder = tradingUpdateUseCase
                .cancelReservationOrder(command);
        return tradingDataMapper.reservationOrderToCancelOrderResponse(reservationOrder);
    }
}
