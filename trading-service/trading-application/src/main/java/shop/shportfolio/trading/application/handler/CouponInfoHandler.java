package shop.shportfolio.trading.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.dto.coupon.CouponKafkaResponse;
import shop.shportfolio.trading.application.ports.output.repository.TradingCouponRepositoryPort;
import shop.shportfolio.trading.domain.UserBalanceDomainService;
import shop.shportfolio.trading.domain.entity.CouponInfo;

import java.util.Optional;

@Slf4j
@Component
public class CouponInfoHandler {

    private final TradingCouponRepositoryPort tradingCouponRepositoryPort;
    private final UserBalanceDomainService userBalanceDomainService;

    @Autowired
    public CouponInfoHandler(TradingCouponRepositoryPort tradingCouponRepositoryPort,
                             UserBalanceDomainService userBalanceDomainService) {
        this.tradingCouponRepositoryPort = tradingCouponRepositoryPort;
        this.userBalanceDomainService = userBalanceDomainService;
    }

    public CouponInfo saveCouponInfo(CouponKafkaResponse couponKafkaResponse) {
        CouponInfo couponInfo = userBalanceDomainService.createCouponInfo(couponKafkaResponse.getCouponId(),
                couponKafkaResponse.getOwner(), couponKafkaResponse.getFeeDiscount()
                , couponKafkaResponse.getIssuedAt(), couponKafkaResponse.getExpiryDate());
        return tradingCouponRepositoryPort.saveCouponInfo(couponInfo);
    }

    public Optional<CouponInfo> trackCouponInfo(UserId userId) {
        return tradingCouponRepositoryPort.findCouponInfoByUserId(userId.getValue());
    }

    public void deleteCoupon(CouponKafkaResponse response) {
        this.trackCouponInfo(response.getOwner()).ifPresent(tradingCouponRepositoryPort::deleteCouponInfoByUserId);
    }

}
