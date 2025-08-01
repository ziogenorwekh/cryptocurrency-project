package shop.shportfolio.portfolio.api.resources;

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
public class DepositWithdrawalResources {

    private final PortfolioApplicationService portfolioApplicationService;

    @Autowired
    public DepositWithdrawalResources(PortfolioApplicationService portfolioApplicationService) {
        this.portfolioApplicationService = portfolioApplicationService;
    }

    @RequestMapping(path = "/portfolio/deposit",method = RequestMethod.POST)
    public ResponseEntity<DepositCreatedResponse> deposit(@RequestBody DepositCreateCommand depositCreateCommand
    ,@RequestHeader("X-header-User-Id") UUID userId) {
        depositCreateCommand.setUserId(userId);
        DepositCreatedResponse response = portfolioApplicationService.deposit(depositCreateCommand);
        log.info("[PORTFOLIO API] request deposit -> {}",response.getUserId());
        return ResponseEntity.ok(response);
    }

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
