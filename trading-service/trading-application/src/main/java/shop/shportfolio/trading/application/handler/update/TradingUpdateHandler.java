package shop.shportfolio.trading.application.handler.update;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.ports.output.redis.TradingOrderRedisPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
import shop.shportfolio.trading.application.support.RedisKeyPrefix;
import shop.shportfolio.trading.domain.OrderDomainService;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;


@Slf4j
@Component
public class TradingUpdateHandler {

    private final TradingOrderRepositoryPort tradingOrderRepositoryPort;
    private final OrderDomainService orderDomainService;
    private final TradingOrderRedisPort tradingOrderRedisPort;

    @Autowired
    public TradingUpdateHandler(TradingOrderRepositoryPort tradingOrderRepositoryPort,
                                OrderDomainService orderDomainService,
                                TradingOrderRedisPort tradingOrderRedisPort) {
        this.tradingOrderRepositoryPort = tradingOrderRepositoryPort;
        this.orderDomainService = orderDomainService;
        this.tradingOrderRedisPort = tradingOrderRedisPort;
    }

    public LimitOrder cancelLimitOrder(LimitOrder limitOrder) {
        orderDomainService.cancelOrder(limitOrder);
        log.info("cancel limit order id {} and status : {}",
                limitOrder.getId().getValue(),limitOrder.getOrderStatus());
//        tradingOrderRedisPort.deleteLimitOrder(RedisKeyPrefix.limit(limitOrder.getMarketId().getValue(), limitOrder
//                .getId().getValue()));
        return tradingOrderRepositoryPort.saveLimitOrder(limitOrder);
    }

    public ReservationOrder cancelReservationOrder(ReservationOrder reservationOrder) {
        orderDomainService.cancelOrder(reservationOrder);
        log.info("cancel reservation order id {} and status : {}",
                reservationOrder.getId().getValue(),reservationOrder.getOrderStatus());
//        tradingOrderRedisPort.deleteReservationOrder(RedisKeyPrefix.reservation(reservationOrder.getMarketId().getValue(),
//                reservationOrder.getId().getValue()));
        return tradingOrderRepositoryPort.saveReservationOrder(reservationOrder);
    }
}
