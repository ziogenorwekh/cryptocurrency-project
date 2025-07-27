package shop.shportfolio.portfolio.application.command;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositCreateCommand {

    @NotNull(message = "userId는 필수입니다.")
    private UUID userId;

    @Min(value = 1, message = "amount는 1 이상이어야 합니다.")
    private long amount;

    @NotBlank(message = "orderId는 필수입니다.")
    private String orderId;

    @NotBlank(message = "paymentKey는 필수입니다.")
    private String paymentKey;
}
