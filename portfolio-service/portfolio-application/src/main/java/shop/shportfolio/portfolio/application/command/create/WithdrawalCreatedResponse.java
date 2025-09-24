package shop.shportfolio.portfolio.application.command.create;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class WithdrawalCreatedResponse {

//    @NotNull(message = "사용자 ID는 필수입니다.")
    private final UUID userId;

//    @NotNull(message = "출금 금액은 필수입니다.")
//    @Min(value = 0, message = "출금 금액은 0 이상이어야 합니다.")
    private final Long withdrawalAmount;

//    @NotNull(message = "거래 시간은 필수입니다.")
//    @PastOrPresent(message = "거래 시간은 현재 또는 과거여야 합니다.")
    private final LocalDateTime transactionTime;

//    @NotBlank(message = "메시지는 필수입니다.")
    private final String message;
}
