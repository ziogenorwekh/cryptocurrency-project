package shop.shportfoilo.coupon.domain.event;

import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfolio.common.domain.event.DomainEvent;
import shop.shportfolio.common.domain.valueobject.MessageType;

import java.time.ZonedDateTime;

public class CouponExpiredEvent extends DomainEvent<Coupon> {
    public CouponExpiredEvent(Coupon domainType, MessageType messageType, ZonedDateTime publishedAt) {
        super(domainType, messageType, publishedAt);
    }
}
