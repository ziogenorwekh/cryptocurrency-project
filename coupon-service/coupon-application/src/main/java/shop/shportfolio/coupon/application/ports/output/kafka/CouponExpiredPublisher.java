package shop.shportfolio.coupon.application.ports.output.kafka;

import shop.shportfoilo.coupon.domain.event.CouponExpiredEvent;
import shop.shportfolio.common.domain.event.DomainEventPublisher;

public interface CouponExpiredPublisher extends DomainEventPublisher<CouponExpiredEvent> {
}
