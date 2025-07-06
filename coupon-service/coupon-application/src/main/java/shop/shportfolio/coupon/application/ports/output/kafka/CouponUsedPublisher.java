package shop.shportfolio.coupon.application.ports.output.kafka;

import shop.shportfoilo.coupon.domain.event.CouponUsedEvent;
import shop.shportfolio.common.domain.event.DomainEventPublisher;

public interface CouponUsedPublisher extends DomainEventPublisher<CouponUsedEvent> {

}
