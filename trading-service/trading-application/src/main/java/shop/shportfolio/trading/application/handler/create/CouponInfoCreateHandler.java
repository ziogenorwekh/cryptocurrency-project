package shop.shportfolio.trading.application.handler.create;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.dto.coupon.CouponKafkaResponse;
import shop.shportfolio.trading.application.ports.output.repository.TradingCouponRepositoryPort;
import shop.shportfolio.trading.domain.UserBalanceDomainService;
import shop.shportfolio.trading.domain.entity.CouponInfo;

@Component
public class CouponInfoCreateHandler {

    private final UserBalanceDomainService userBalanceDomainService;
    private final TradingCouponRepositoryPort tradingCouponRepositoryPort;

    @Autowired
    public CouponInfoCreateHandler(UserBalanceDomainService userBalanceDomainService,
                                   TradingCouponRepositoryPort tradingCouponRepositoryPort) {
        this.userBalanceDomainService = userBalanceDomainService;
        this.tradingCouponRepositoryPort = tradingCouponRepositoryPort;
    }


    public CouponInfo saveCouponInfo(CouponKafkaResponse couponKafkaResponse) {
        CouponInfo couponInfo = userBalanceDomainService.createCouponInfo(couponKafkaResponse.getCouponId(),
                couponKafkaResponse.getOwner(), couponKafkaResponse.getFeeDiscount()
                , couponKafkaResponse.getIssuedAt(), couponKafkaResponse.getExpiryDate());
        return tradingCouponRepositoryPort.saveCouponInfo(couponInfo);
    }
}
