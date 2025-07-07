package shop.shportfolio.trading.application.handler.update;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.ports.output.redis.TradingOrderRedisPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
import shop.shportfolio.trading.application.support.RedisKeyPrefix;
import shop.shportfolio.trading.domain.TradingDomainService;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;


@Slf4j
@Component
public class TradingUpdateHandler {

    private final TradingOrderRepositoryPort tradingOrderRepositoryPort;
    private final TradingDomainService tradingDomainService;
    private final TradingOrderRedisPort tradingOrderRedisPort;

    @Autowired
    public TradingUpdateHandler(TradingOrderRepositoryPort tradingOrderRepositoryPort,
                                TradingDomainService tradingDomainService,
                                TradingOrderRedisPort tradingOrderRedisPort) {
        this.tradingOrderRepositoryPort = tradingOrderRepositoryPort;
        this.tradingDomainService = tradingDomainService;
        this.tradingOrderRedisPort = tradingOrderRedisPort;
    }

    public LimitOrder cancelLimitOrder(LimitOrder limitOrder) {
        tradingDomainService.cancelOrder(limitOrder);
        log.info("cancel limit order id {} and status : {}",
                limitOrder.getId().getValue(),limitOrder.getOrderStatus());
        tradingOrderRedisPort.deleteLimitOrder(RedisKeyPrefix.limit(limitOrder.getMarketId().getValue(), limitOrder
                .getId().getValue()));
        return tradingOrderRepositoryPort.saveLimitOrder(limitOrder);
    }

    public ReservationOrder cancelReservationOrder(ReservationOrder reservationOrder) {
        tradingDomainService.cancelOrder(reservationOrder);
        log.info("cancel reservation order id {} and status : {}",
                reservationOrder.getId().getValue(),reservationOrder.getOrderStatus());
        return tradingOrderRepositoryPort.saveReservationOrder(reservationOrder);
    }
}
