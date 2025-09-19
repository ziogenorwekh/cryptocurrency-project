package shop.shportfolio.portfolio.application.command.create;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class DepositCreatedResponse {

    @NotNull(message = "포트폴리오 ID는 필수입니다.")
    private final UUID portfolioId;

    @NotNull(message = "잔액 ID는 필수입니다.")
    private final UUID balanceId;

    @NotNull(message = "사용자 ID는 필수입니다.")
    private final UUID userId;

    @NotNull(message = "입금 금액은 필수입니다.")
    @Min(value = 0, message = "적어도 0원 이상이어야 합니다.")
    private final Long paidAmount;

    @NotNull(message = "발급 시간은 필수입니다.")
    @PastOrPresent(message = "발급 시간은 현재 또는 과거여야 합니다.")
    private final LocalDateTime issuedAt;
}
