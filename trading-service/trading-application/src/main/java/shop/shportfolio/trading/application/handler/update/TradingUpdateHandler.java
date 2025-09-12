package shop.shportfolio.trading.application.handler.update;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
import shop.shportfolio.trading.domain.OrderDomainService;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.event.LimitOrderCanceledEvent;
import shop.shportfolio.trading.domain.event.ReservationOrderCanceledEvent;

import java.util.UUID;


@Slf4j
@Component
public class TradingUpdateHandler {

    private final TradingOrderRepositoryPort tradingOrderRepositoryPort;
    private final OrderDomainService orderDomainService;

    @Autowired
    public TradingUpdateHandler(TradingOrderRepositoryPort tradingOrderRepositoryPort,
                                OrderDomainService orderDomainService) {
        this.tradingOrderRepositoryPort = tradingOrderRepositoryPort;
        this.orderDomainService = orderDomainService;
    }

    public LimitOrderCanceledEvent cancelPendingLimitOrder(LimitOrder limitOrder) {
        LimitOrderCanceledEvent limitOrderCanceledEvent = orderDomainService
                .cancelPendingOrder(limitOrder);
        tradingOrderRepositoryPort.saveLimitOrder(limitOrder);
        return limitOrderCanceledEvent;
    }

    public ReservationOrderCanceledEvent cancelPendingReservationOrder(ReservationOrder reservationOrder) {
        ReservationOrderCanceledEvent reservationOrderCanceledEvent = orderDomainService
                .cancelPendingOrder(reservationOrder);
        tradingOrderRepositoryPort.saveReservationOrder(reservationOrder);
        return reservationOrderCanceledEvent;
    }

    public void cancelLimitOrder(String limitOrderId, UUID userId) {
        tradingOrderRepositoryPort.findLimitOrderByOrderIdAndUserId(limitOrderId, userId)
                .ifPresent(limitOrder -> {
                    log.info("cancel limit order id {} and status : {}",
                            limitOrder.getId().getValue(), limitOrder.getOrderStatus());
                    orderDomainService.cancelOrder(limitOrder);
                    tradingOrderRepositoryPort.saveLimitOrder(limitOrder);
                });
    }

    public void cancelReservationOrder(String reservationOrderId, UUID userId) {
        tradingOrderRepositoryPort.findReservationOrderByOrderIdAndUserId(reservationOrderId, userId)
                .ifPresent(reservationOrder -> {
                    log.info("cancel reservation order id {} and status : {}",
                            reservationOrder.getId().getValue(), reservationOrder.getOrderStatus());
                    orderDomainService.cancelOrder(reservationOrder);
                    tradingOrderRepositoryPort.saveReservationOrder(reservationOrder);
                });
    }

    public void compensationCancelLimitOrder(String limitOrderId, UUID userId) {
        tradingOrderRepositoryPort.findLimitOrderByOrderIdAndUserId(limitOrderId, userId)
                .ifPresent(limitOrder -> {
                    orderDomainService.revertCancel(limitOrder);
                    tradingOrderRepositoryPort.saveLimitOrder(limitOrder);
                });
    }

    public void compensationCancelReservationOrder(String reservationOrderId, UUID userId) {
        tradingOrderRepositoryPort.findReservationOrderByOrderIdAndUserId(reservationOrderId, userId)
                .ifPresent(reservationOrder -> {
                    orderDomainService.revertCancel(reservationOrder);
                    tradingOrderRepositoryPort.saveReservationOrder(reservationOrder);
                });
    }
}
