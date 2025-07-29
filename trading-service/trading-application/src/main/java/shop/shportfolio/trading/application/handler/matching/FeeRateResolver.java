package shop.shportfolio.trading.application.handler.matching;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.FeeDiscount;
import shop.shportfolio.common.domain.valueobject.FeeRate;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.application.handler.CouponInfoHandler;
import shop.shportfolio.trading.application.policy.FeePolicy;
import shop.shportfolio.trading.domain.entity.CouponInfo;
import shop.shportfolio.trading.domain.valueobject.OrderSide;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeeRateResolver {

    private final FeePolicy feePolicy;
    private final CouponInfoHandler couponInfoHandler;

    /**
     * UserId, OrderSide를 기준으로 정책 + 쿠폰을 반영한 최종 FeeRate를 반환
     */
    public FeeRate resolve(UserId userId, OrderSide orderSide) {
        FeeRate baseFeeRate = feePolicy.calculateDefualtFeeRate(orderSide);
        Optional<CouponInfo> couponInfoOptional = couponInfoHandler.trackCouponInfo(userId);

//        if (couponInfoOptional.isPresent()) {
//            CouponInfo couponInfo = couponInfoOptional.get();
//            if (!couponInfo.getUsageExpiryDate().isExpired()) {
//                BigDecimal discountRatio = couponInfo.getFeeDiscount().getRatio();
//                log.info("Coupon applied: userId={}, discount={}", userId.getValue(), discountRatio);
//                return baseFeeRate.applyDiscount(discountRatio);
//            }
//        }
//        return baseFeeRate;
        return couponInfoOptional
                .map(CouponInfo::getFeeDiscount)
                .map(FeeDiscount::getRatio)
                .map(baseFeeRate::applyDiscount)
                .orElse(baseFeeRate);
    }
}