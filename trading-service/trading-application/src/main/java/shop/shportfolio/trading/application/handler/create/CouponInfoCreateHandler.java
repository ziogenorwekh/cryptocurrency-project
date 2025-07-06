package shop.shportfolio.trading.application.handler.create;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.dto.coupon.CouponResponse;
import shop.shportfolio.trading.application.ports.output.repository.TradingCouponRepositoryPort;
import shop.shportfolio.trading.domain.TradingDomainService;
import shop.shportfolio.trading.domain.entity.CouponInfo;

@Component
public class CouponInfoCreateHandler {

    private final TradingDomainService tradingDomainService;
    private final TradingCouponRepositoryPort tradingCouponRepositoryPort;

    @Autowired
    public CouponInfoCreateHandler(TradingDomainService tradingDomainService,
                                   TradingCouponRepositoryPort tradingCouponRepositoryPort) {
        this.tradingDomainService = tradingDomainService;
        this.tradingCouponRepositoryPort = tradingCouponRepositoryPort;
    }


    public CouponInfo saveCouponInfo(CouponResponse couponResponse) {
        CouponInfo couponInfo = tradingDomainService.createCouponInfo(couponResponse.getCouponId(),
                couponResponse.getOwner(), couponResponse.getFeeDiscount()
                , couponResponse.getIssuedAt(), couponResponse.getExpiryDate());
        return tradingCouponRepositoryPort.saveCouponInfo(couponInfo);
    }
}
