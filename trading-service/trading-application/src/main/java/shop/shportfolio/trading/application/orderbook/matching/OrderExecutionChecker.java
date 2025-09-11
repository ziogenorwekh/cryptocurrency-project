//package shop.shportfolio.trading.application.orderbook.matching;
//
//import org.springframework.stereotype.Component;
//import shop.shportfolio.common.domain.valueobject.OrderPrice;
//import shop.shportfolio.trading.domain.OrderDomainService;
//import shop.shportfolio.trading.domain.entity.Order;
//import shop.shportfolio.trading.domain.entity.ReservationOrder;
//import shop.shportfolio.trading.domain.valueobject.TickPrice;
//
//import java.time.LocalDateTime;
//import java.time.ZoneOffset;
//
//@Component
//public class OrderExecutionChecker {
//
//    private final OrderDomainService orderDomainService;
//
//    public OrderExecutionChecker(OrderDomainService orderDomainService) {
//        this.orderDomainService = orderDomainService;
//    }
//
//    /**
//     * 현재 시점 기준으로 만료 여부 판별
//     */
//    public boolean isExpired(ReservationOrder order) {
//        return order.isExpired(LocalDateTime.now(ZoneOffset.UTC));
//    }
//
//    public boolean canMatchPrice(Order order, TickPrice tickPrice) {
//        return orderDomainService.canMatchPrice(order, tickPrice);
//    }
//    /**
//     * 트리거 조건, 가격 조건 등을 모두 검사하여 실행 가능 여부 판별
//     */
//    public boolean isExecutable(ReservationOrder reservationOrder, OrderPrice restingOrderPrice) {
//        return orderDomainService.isReservationOrderExecutable(reservationOrder, restingOrderPrice);
//    }
//}
