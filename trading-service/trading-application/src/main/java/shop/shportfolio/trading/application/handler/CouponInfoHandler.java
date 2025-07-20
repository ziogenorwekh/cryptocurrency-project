package shop.shportfolio.trading.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.ports.output.repository.TradingCouponRepositoryPort;
import shop.shportfolio.trading.domain.entity.CouponInfo;

import java.util.Optional;

@Slf4j
@Component
public class CouponInfoHandler {

    private final TradingCouponRepositoryPort tradingCouponRepositoryPort;

    @Autowired
    public CouponInfoHandler(TradingCouponRepositoryPort tradingCouponRepositoryPort) {
        this.tradingCouponRepositoryPort = tradingCouponRepositoryPort;
    }

    public Optional<CouponInfo> trackCouponInfo(UserId userId) {
        return tradingCouponRepositoryPort.findCouponInfoByUserId(userId.getValue());
    }

}
