package shop.shportfolio.portfolio.api.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.shportfolio.portfolio.application.command.create.DepositCreateCommand;
import shop.shportfolio.portfolio.application.command.create.DepositCreatedResponse;
import shop.shportfolio.portfolio.application.command.create.WithdrawalCreateCommand;
import shop.shportfolio.portfolio.application.command.create.WithdrawalCreatedResponse;
import shop.shportfolio.portfolio.application.port.input.PortfolioApplicationService;

import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "입출금 API", description = "사용자의 포트폴리오 입출금 처리 API")
public class DepositWithdrawalResources {

    private final PortfolioApplicationService portfolioApplicationService;

    @Autowired
    public DepositWithdrawalResources(PortfolioApplicationService portfolioApplicationService) {
        this.portfolioApplicationService = portfolioApplicationService;
    }

    @Operation(
            summary = "입금 처리",
            description = "사용자의 포트폴리오에 입금을 처리합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "입금 성공",
                            content = @Content(schema = @Schema(implementation = DepositCreatedResponse.class)))
            }
    )
    @RequestMapping(path = "/portfolio/deposit",method = RequestMethod.POST)
    public ResponseEntity<DepositCreatedResponse> deposit(@RequestBody DepositCreateCommand depositCreateCommand
    ,@RequestHeader("X-header-User-Id") UUID userId) {
        depositCreateCommand.setUserId(userId);
        DepositCreatedResponse response = portfolioApplicationService.deposit(depositCreateCommand);
        log.info("[PORTFOLIO API] request deposit -> {}",response.getUserId());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "출금 처리",
            description = "사용자의 포트폴리오에서 출금을 처리합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "출금 성공",
                            content = @Content(schema = @Schema(implementation = WithdrawalCreatedResponse.class)))
            }
    )
    @RequestMapping(path = "/portfolio/withdrawal",method = RequestMethod.POST)
    public ResponseEntity<WithdrawalCreatedResponse> withdrawal(@RequestBody
                                                                    WithdrawalCreateCommand withdrawalCreateCommand,
                                                                @RequestHeader("X-header-User-Id") UUID userId) {
        withdrawalCreateCommand.setUserId(userId);
        WithdrawalCreatedResponse response = portfolioApplicationService.withdrawal(withdrawalCreateCommand);
        log.info("[PORTFOLIO API] request withdrawal -> {}",response.getUserId());
        return ResponseEntity.ok(response);

    }
}
