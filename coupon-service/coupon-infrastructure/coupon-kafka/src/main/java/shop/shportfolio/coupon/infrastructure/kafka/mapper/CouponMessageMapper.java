package shop.shportfolio.coupon.infrastructure.kafka.mapper;

import org.springframework.stereotype.Component;
import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfolio.common.avro.CouponAvroModel;
import shop.shportfolio.common.domain.dto.CouponData;
import shop.shportfolio.common.domain.valueobject.MessageType;

@Component
public class CouponMessageMapper {

    public CouponAvroModel couponDataToCouponAvroModel(CouponData couponData, MessageType messageType) {
        return CouponAvroModel.newBuilder()
                .setCouponId(couponData.getCouponId().getValue().toString())
                .setOwner(couponData.getOwner().getValue().toString())
                .setFeeDiscount(couponData.getFeeDiscount().getValue())
                .setIssuedAt(couponData.getIssuedAt().getValue())
                .setExpiryDate(couponData.getExpiryDate().getValue())
                .setMessageType(mapToAvroMessageType(messageType))
                .build();
    }

    public CouponAvroModel couponToCouponAvroModel(Coupon couponData, MessageType messageType) {
        return CouponAvroModel.newBuilder()
                .setCouponId(couponData.getId().getValue().toString())
                .setOwner(couponData.getOwner().getValue().toString())
                .setFeeDiscount(couponData.getFeeDiscount().getValue())
                .setIssuedAt(couponData.getIssuedAt().getValue())
                .setExpiryDate(couponData.getValidUntil().getValue())
                .setMessageType(mapToAvroMessageType(messageType))
                .build();
    }
    private shop.shportfolio.common.avro.MessageType mapToAvroMessageType(MessageType type) {
        return switch (type) {
            case CREATE -> shop.shportfolio.common.avro.MessageType.CREATE;
            case DELETE -> shop.shportfolio.common.avro.MessageType.DELETE;
            case FAIL -> shop.shportfolio.common.avro.MessageType.FAIL;
            case REJECT -> shop.shportfolio.common.avro.MessageType.REJECT;
            case UPDATE -> shop.shportfolio.common.avro.MessageType.UPDATE;
            case NO_DEF -> shop.shportfolio.common.avro.MessageType.NO_DEF;
        };
    }
}
