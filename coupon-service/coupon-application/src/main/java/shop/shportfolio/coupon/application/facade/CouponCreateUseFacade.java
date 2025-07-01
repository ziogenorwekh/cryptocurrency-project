package shop.shportfolio.coupon.application.facade;

import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfolio.coupon.application.command.create.CouponCreateCommand;
import shop.shportfolio.coupon.application.ports.input.CouponCreateUseCase;

public class CouponCreateUseFacade implements CouponCreateUseCase {


    @Override
    public Coupon create(CouponCreateCommand command) {
        return null;
    }
}
