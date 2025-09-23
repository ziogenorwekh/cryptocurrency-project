package shop.shportfolio.trading.application;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import shop.shportfolio.trading.domain.entity.*;

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

    @Autowired
    public TradingApplicationServiceImpl(TradingCreateOrderUseCase createOrderUseCase,
                                         TradingTrackUseCase tradingTrackUseCase,
                                         TradingDataMapper tradingDataMapper,
                                         TradingUpdateUseCase tradingUpdateUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.tradingTrackUseCase = tradingTrackUseCase;
        this.tradingDataMapper = tradingDataMapper;
        this.tradingUpdateUseCase = tradingUpdateUseCase;
    }

    @Override
    @Transactional
    public CreateLimitOrderResponse createLimitOrder(@Valid CreateLimitOrderCommand createLimitOrderCommand) {
        LimitOrder limitOrder = createOrderUseCase.createLimitOrder(createLimitOrderCommand);
        return tradingDataMapper.limitOrderToCreateLimitOrderResponse(limitOrder);
    }

    @Override
    @Transactional
    public CreateMarketOrderResponse createMarketOrder(@Valid CreateMarketOrderCommand createMarketOrderCommand) {
        MarketOrder marketOrder = createOrderUseCase.createMarketOrder(createMarketOrderCommand);
        return tradingDataMapper.marketOrderToCreateMarketOrderResponse(marketOrder);
    }

    @Override
    @Transactional
    public CreateReservationResponse createReservationOrder(@Valid CreateReservationOrderCommand command) {
        ReservationOrder reservationOrder = createOrderUseCase.createReservationOrder(command);
        log.info("created Reservation Order ID: {} in Services", reservationOrder.getId().getValue());
        CreateReservationResponse response = tradingDataMapper.reservationOrderToCreateReservationResponse(reservationOrder);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public LimitOrderTrackResponse findLimitOrderTrackByOrderIdAndUserId(@Valid LimitOrderTrackQuery limitOrderTrackQuery) {
        LimitOrder limitOrder = tradingTrackUseCase.findLimitOrderByOrderId(limitOrderTrackQuery);
        return tradingDataMapper.limitOrderTrackToLimitOrderTrackResponse(limitOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationOrderTrackResponse findReservationOrderTrackByOrderIdAndUserId(
            @Valid ReservationOrderTrackQuery query) {
        ReservationOrder order = tradingTrackUseCase.findReservationOrderByOrderIdAndUserId(query);
        return tradingDataMapper.reservationOrderToReservationOrderTrackResponse(order);
    }

    @Override
    @Transactional
    public CancelOrderResponse cancelRequestLimitOrder(@Valid CancelLimitOrderCommand cancelLimitOrderCommand) {
        LimitOrder limitOrder = tradingUpdateUseCase.pendingCancelLimitOrder(cancelLimitOrderCommand);
        return tradingDataMapper.limitOrderToCancelOrderResponse(limitOrder);
    }

    @Override
    @Transactional
    public CancelOrderResponse cancelRequestReservationOrder(@Valid CancelReservationOrderCommand command) {
        ReservationOrder reservationOrder = tradingUpdateUseCase
                .pendingCancelReservationOrder(command);
        return tradingDataMapper.reservationOrderToCancelOrderResponse(reservationOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderTrackResponse> findAllOrderByMarketId(@Valid OrderTrackQuery orderTrackQuery) {
        return tradingTrackUseCase.findAllOrderByMarketId(orderTrackQuery)
                .stream().map(tradingDataMapper::orderToOrderTrackResponse)
                .collect(Collectors.toList());
    }
}
