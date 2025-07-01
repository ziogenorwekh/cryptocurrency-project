package shop.shportfolio.coupon.application.ports.input;

import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfolio.coupon.application.command.create.CouponCreateCommand;

public interface CouponCreateUseCase {

    Coupon create(CouponCreateCommand command);
}
