package shop.shportfolio.coupon.infrastructure.kafka.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.CouponAvroModel;
import shop.shportfolio.common.domain.dto.CouponData;

@Component
public class CouponMessageMapper {

    public CouponAvroModel couponDataToCouponAvroModel(CouponData couponData) {
        return CouponAvroModel.newBuilder()
                .setCouponId(couponData.getCouponId().getValue().toString())
                .setOwner(couponData.getOwner().getValue().toString())
                .setFeeDiscount(couponData.getFeeDiscount().getValue())
                .setIssuedAt(couponData.getIssuedAt().getValue())
                .setExpiryDate(couponData.getExpiryDate().getValue())
                .build();
    }
}
