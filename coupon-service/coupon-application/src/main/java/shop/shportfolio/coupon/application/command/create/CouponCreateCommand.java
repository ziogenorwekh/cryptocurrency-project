package shop.shportfolio.coupon.application.command.create;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shop.shportfolio.common.domain.valueobject.RoleType;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponCreateCommand {

    @NotNull(message = "userId는 필수입니다.")
    private UUID userId;

    @NotNull(message = "roles는 필수입니다.")
    private List<RoleType> roles;

    @Min(value = 1, message = "amount는 1 이상이어야 합니다.")
    private String amount;

    @NotBlank(message = "orderId는 필수입니다.")
    private String orderId;

    @NotBlank(message = "paymentKey는 필수입니다.")
    private String paymentKey;
}
