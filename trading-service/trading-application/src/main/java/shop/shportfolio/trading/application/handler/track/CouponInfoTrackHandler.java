package shop.shportfolio.trading.application.handler.track;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.application.ports.output.repository.TradingCouponRepositoryPort;
import shop.shportfolio.trading.domain.entity.CouponInfo;

import java.util.Optional;

@Component
public class CouponInfoTrackHandler {

    private final TradingCouponRepositoryPort tradingCouponRepositoryPort;

    @Autowired
    public CouponInfoTrackHandler(TradingCouponRepositoryPort tradingCouponRepositoryPort) {
        this.tradingCouponRepositoryPort = tradingCouponRepositoryPort;
    }

    public Optional<CouponInfo> trackCouponInfo(UserId userId) {
        return tradingCouponRepositoryPort.findCouponInfoByUserId(userId.getValue());
    }
}
