package shop.shportfolio.trading.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import shop.shportfolio.trading.application.command.create.*;
import shop.shportfolio.trading.application.command.track.*;
import shop.shportfolio.trading.application.command.update.CancelLimitOrderCommand;
import shop.shportfolio.trading.application.command.update.CancelOrderResponse;
import shop.shportfolio.trading.application.command.update.CancelReservationOrderCommand;
import shop.shportfolio.trading.application.mapper.TradingDataMapper;
import shop.shportfolio.trading.application.ports.input.*;
import shop.shportfolio.trading.domain.entity.*;

import java.util.List;

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
    public CreateLimitOrderResponse createLimitOrder(CreateLimitOrderCommand createLimitOrderCommand) {
        LimitOrder limitOrder = createOrderUseCase.createLimitOrder(createLimitOrderCommand);
        executeOrderMatchingUseCase.executeLimitOrder(limitOrder);
        return tradingDataMapper.limitOrderToCreateLimitOrderResponse(limitOrder);
    }

    @Override
    public void createMarketOrder(CreateMarketOrderCommand createMarketOrderCommand) {
        MarketOrder marketOrder = createOrderUseCase.createMarketOrder(createMarketOrderCommand);
        executeOrderMatchingUseCase.executeMarketOrder(marketOrder);
    }

    @Override
    public CreateReservationResponse createReservationOrder(CreateReservationOrderCommand command) {
        ReservationOrder reservationOrder = createOrderUseCase.createReservationOrder(command);
        log.info("created Reservation Order ID: {} in Services", reservationOrder.getId().getValue());
        return tradingDataMapper.reservationOrderToCreateReservationResponse(reservationOrder);
    }

    @Override
    public OrderBookTrackResponse findOrderBook(OrderBookTrackQuery orderBookTrackQuery) {
        OrderBook orderBook = tradingTrackUseCase.findOrderBook(orderBookTrackQuery);
        return tradingDataMapper.orderBookToOrderBookTrackResponse(orderBook);
    }

    @Override
    public LimitOrderTrackResponse findLimitOrderTrackByOrderIdAndUserId(LimitOrderTrackQuery limitOrderTrackQuery) {
        LimitOrder limitOrder = tradingTrackUseCase.findLimitOrderByOrderId(limitOrderTrackQuery);
        return tradingDataMapper.limitOrderTrackToLimitOrderTrackResponse(limitOrder);
    }

    @Override
    public ReservationOrderTrackResponse findReservationOrderTrackByOrderIdAndUserId(ReservationOrderTrackQuery query) {
        ReservationOrder order = tradingTrackUseCase.findReservationOrderByOrderIdAndUserId(query);
        return tradingDataMapper.reservationOrderToReservationOrderTrackResponse(order);
    }

    @Override
    public CancelOrderResponse cancelLimitOrder(CancelLimitOrderCommand cancelLimitOrderCommand) {
        LimitOrder limitOrder = tradingUpdateUseCase.cancelLimitOrder(cancelLimitOrderCommand);
        return tradingDataMapper.limitOrderToCancelOrderResponse(limitOrder);
    }

    @Override
    public CancelOrderResponse cancelReservationOrder(CancelReservationOrderCommand command) {
        ReservationOrder reservationOrder = tradingUpdateUseCase
                .cancelReservationOrder(command);
        return tradingDataMapper.reservationOrderToCancelOrderResponse(reservationOrder);
    }

}
