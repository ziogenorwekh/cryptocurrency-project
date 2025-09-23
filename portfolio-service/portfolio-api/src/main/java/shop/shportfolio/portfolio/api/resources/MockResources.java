package shop.shportfolio.portfolio.api.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.shportfolio.portfolio.application.command.create.DepositCreateCommand;
import shop.shportfolio.portfolio.application.command.create.DepositCreatedResponse;
import shop.shportfolio.portfolio.application.port.input.PortfolioApplicationService;

import java.util.UUID;

@Slf4j
@RestController
public class MockResources {

    private final PortfolioApplicationService portfolioApplicationService;

    @Autowired
    public MockResources(PortfolioApplicationService portfolioApplicationService) {
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
    @RequestMapping(path = "/portfolio/mock/deposit",method = RequestMethod.POST)
    public ResponseEntity<DepositCreatedResponse> depositMock(@RequestBody DepositCreateCommand depositCreateCommand
            , @RequestHeader("X-header-User-Id") UUID userId) {
        depositCreateCommand.setUserId(userId);
        DepositCreatedResponse response = portfolioApplicationService.depositMock(depositCreateCommand);
        log.info("[PORTFOLIO API] request deposit -> {}",response.getUserId());
        return ResponseEntity.ok(response);
    }
}
