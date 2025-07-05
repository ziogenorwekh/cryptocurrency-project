package shop.shportfolio.coupon.application.command.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponUseUpdateCommand {

    @NotNull(message = "userId는 필수입니다.")
    private UUID userId;

    @NotNull(message = "couponId는 필수입니다.")
    private UUID couponId;

    @NotBlank(message = "couponCode는 필수입니다.")
    private String couponCode;
}
