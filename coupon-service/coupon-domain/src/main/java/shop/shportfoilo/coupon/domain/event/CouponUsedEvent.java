package shop.shportfoilo.coupon.domain.event;

import shop.shportfolio.common.domain.dto.CouponData;
import shop.shportfolio.common.domain.event.DomainEvent;
import shop.shportfolio.common.domain.valueobject.MessageType;

import java.time.ZonedDateTime;

public class CouponUsedEvent extends DomainEvent<CouponData> {
    public CouponUsedEvent(CouponData domainType, MessageType messageType, ZonedDateTime publishedAt) {
        super(domainType, messageType, publishedAt);
    }
}
