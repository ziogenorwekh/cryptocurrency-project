package shop.shportfolio.coupon.application.command.create;

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
    private UUID userId;
    private List<RoleType> roles;
}
