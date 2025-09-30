package shop.shportfolio.trading.application;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import shop.shportfolio.trading.application.command.create.*;
import shop.shportfolio.trading.application.command.track.request.LimitOrderTrackQuery;
import shop.shportfolio.trading.application.command.track.request.OrderTrackQuery;
import shop.shportfolio.trading.application.command.track.request.ReservationOrderTrackQuery;
import shop.shportfolio.trading.application.command.track.response.LimitOrderTrackResponse;
import shop.shportfolio.trading.application.command.track.response.OrderTrackResponse;
import shop.shportfolio.trading.application.command.track.response.ReservationOrderTrackResponse;
import shop.shportfolio.trading.application.command.update.CancelLimitOrderCommand;
import shop.shportfolio.trading.application.command.update.CancelOrderResponse;
import shop.shportfolio.trading.application.command.update.CancelReservationOrderCommand;
import shop.shportfolio.trading.application.mapper.TradingDataMapper;
import shop.shportfolio.trading.application.ports.input.*;
import shop.shportfolio.trading.application.ports.input.usecase.TradingCreateOrderUseCase;
import shop.shportfolio.trading.application.ports.input.usecase.TradingTrackUseCase;
import shop.shportfolio.trading.application.ports.input.usecase.TradingUpdateUseCase;
import shop.shportfolio.trading.application.ports.output.kafka.*;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.event.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
public class TradingApplicationServiceImpl implements TradingApplicationService {

    private final TradingCreateOrderUseCase createOrderUseCase;
    private final TradingTrackUseCase tradingTrackUseCase;
    private final TradingDataMapper tradingDataMapper;
    private final TradingUpdateUseCase tradingUpdateUseCase;
    private final MarketOrderCreatedPublisher marketOrderCreatedPublisher;
    private final ReservationOrderCreatedPublisher reservationOrderCreatedPublisher;
    private final LimitOrderCreatedPublisher limitOrderCreatedPublisher;
    private final LimitOrderCancelledPublisher limitOrderCancelledPublisher;
    private final ReservationOrderCancelledPublisher reservationOrderCancelledPublisher;

    @Autowired
    public TradingApplicationServiceImpl(TradingCreateOrderUseCase createOrderUseCase,
                                         TradingTrackUseCase tradingTrackUseCase,
                                         TradingDataMapper tradingDataMapper,
                                         TradingUpdateUseCase tradingUpdateUseCase,
                                         MarketOrderCreatedPublisher marketOrderCreatedPublisher,
                                         ReservationOrderCreatedPublisher reservationOrderCreatedPublisher,
                                         LimitOrderCreatedPublisher limitOrderCreatedPublisher,
                                         LimitOrderCancelledPublisher limitOrderCancelledPublisher,
                                         ReservationOrderCancelledPublisher reservationOrderCancelledPublisher) {
        this.createOrderUseCase = createOrderUseCase;
        this.tradingTrackUseCase = tradingTrackUseCase;
        this.tradingDataMapper = tradingDataMapper;
        this.tradingUpdateUseCase = tradingUpdateUseCase;
        this.marketOrderCreatedPublisher = marketOrderCreatedPublisher;
        this.reservationOrderCreatedPublisher = reservationOrderCreatedPublisher;
        this.limitOrderCreatedPublisher = limitOrderCreatedPublisher;
        this.limitOrderCancelledPublisher = limitOrderCancelledPublisher;
        this.reservationOrderCancelledPublisher = reservationOrderCancelledPublisher;
    }

    @Override
    public CreateLimitOrderResponse createLimitOrder(@Valid CreateLimitOrderCommand createLimitOrderCommand) {
        LimitOrderCreatedEvent limitOrderCreatedEvent = createOrderUseCase.createLimitOrder(createLimitOrderCommand);
        LimitOrder limitOrder = limitOrderCreatedEvent.getDomainType();
        limitOrderCreatedPublisher.publish(limitOrderCreatedEvent);
        return tradingDataMapper.limitOrderToCreateLimitOrderResponse(limitOrder);
    }

    @Override
    public CreateMarketOrderResponse createMarketOrder(@Valid CreateMarketOrderCommand createMarketOrderCommand) {
        MarketOrderCreatedEvent marketOrderCreatedEvent = createOrderUseCase.createMarketOrder(createMarketOrderCommand);
        MarketOrder marketOrder = marketOrderCreatedEvent.getDomainType();
        log.info("created Market Order ID: {} in Services", marketOrder.getId().getValue());
        marketOrderCreatedPublisher.publish(marketOrderCreatedEvent);
        return tradingDataMapper.marketOrderToCreateMarketOrderResponse(marketOrder);
    }

    @Override
    public CreateReservationResponse createReservationOrder(@Valid CreateReservationOrderCommand command) {
        ReservationOrderCreatedEvent reservationOrderCreatedEvent = createOrderUseCase.createReservationOrder(command);
        ReservationOrder reservationOrder = reservationOrderCreatedEvent.getDomainType();
        log.info("created Reservation Order ID: {} in Services", reservationOrder.getId().getValue());
        reservationOrderCreatedPublisher.publish(reservationOrderCreatedEvent);
        CreateReservationResponse response = tradingDataMapper.reservationOrderToCreateReservationResponse(reservationOrder);
        return response;
    }

    @Override
    public LimitOrderTrackResponse findLimitOrderTrackByOrderIdAndUserId(@Valid LimitOrderTrackQuery limitOrderTrackQuery) {
        LimitOrder limitOrder = tradingTrackUseCase.findLimitOrderByOrderId(limitOrderTrackQuery);
        return tradingDataMapper.limitOrderTrackToLimitOrderTrackResponse(limitOrder);
    }

    @Override
    public ReservationOrderTrackResponse findReservationOrderTrackByOrderIdAndUserId(
            @Valid ReservationOrderTrackQuery query) {
        ReservationOrder order = tradingTrackUseCase.findReservationOrderByOrderIdAndUserId(query);
        return tradingDataMapper.reservationOrderToReservationOrderTrackResponse(order);
    }

    @Override
    public CancelOrderResponse cancelRequestLimitOrder(@Valid CancelLimitOrderCommand cancelLimitOrderCommand) {
        LimitOrderCanceledEvent limitOrderCanceledEvent = tradingUpdateUseCase.pendingCancelLimitOrder(
                cancelLimitOrderCommand);
        limitOrderCancelledPublisher.publish(limitOrderCanceledEvent);
        return tradingDataMapper.limitOrderToCancelOrderResponse(limitOrderCanceledEvent.getDomainType());
    }

    @Override
    public CancelOrderResponse cancelRequestReservationOrder(@Valid CancelReservationOrderCommand command) {
        ReservationOrderCanceledEvent reservationOrderCanceledEvent = tradingUpdateUseCase
                .pendingCancelReservationOrder(command);
        reservationOrderCancelledPublisher.publish(reservationOrderCanceledEvent);
        return tradingDataMapper.reservationOrderToCancelOrderResponse(reservationOrderCanceledEvent.getDomainType());
    }

    @Override
    public List<OrderTrackResponse> findAllOrderByMarketId(@Valid OrderTrackQuery orderTrackQuery) {
        return tradingTrackUseCase.findAllOrderByMarketId(orderTrackQuery)
                .stream().map(tradingDataMapper::orderToOrderTrackResponse)
                .collect(Collectors.toList());
    }
}
