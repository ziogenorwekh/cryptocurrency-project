package shop.shportfolio.coupon.application.command.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponCancelUpdateCommand {

    private UUID userId;
    private UUID couponId;
    private String cancelReason;
}
