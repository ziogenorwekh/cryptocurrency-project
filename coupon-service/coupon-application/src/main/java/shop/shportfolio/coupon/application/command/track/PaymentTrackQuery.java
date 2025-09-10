package shop.shportfolio.coupon.application.command.track;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTrackQuery {

    @NotNull(message = "userId는 필수입니다.")
    private UUID userId;
    @NotNull(message = "couponId는 필수입니다.")
    private UUID couponId;
}
