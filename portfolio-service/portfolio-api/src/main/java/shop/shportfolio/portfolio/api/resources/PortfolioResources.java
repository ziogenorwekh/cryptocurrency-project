package shop.shportfolio.portfolio.api.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.shportfolio.portfolio.application.command.create.PortfolioCreateCommand;
import shop.shportfolio.portfolio.application.command.create.PortfolioCreatedResponse;
import shop.shportfolio.portfolio.application.command.track.*;
import shop.shportfolio.portfolio.application.port.input.PortfolioApplicationService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/portfolio")
@Tag(name = "Portfolio API", description = "사용자 포트폴리오 관련 API")
public class PortfolioResources {

    private final PortfolioApplicationService portfolioApplicationService;

    @Autowired
    public PortfolioResources(PortfolioApplicationService portfolioApplicationService) {
        this.portfolioApplicationService = portfolioApplicationService;
    }

    @Operation(
            summary = "암호화폐 보유 수량 조회",
            description = "지정한 마켓에서 사용자의 암호화폐 보유 수량을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = CryptoBalanceTrackQueryResponse.class)))
            }
    )
    @RequestMapping(path = "/crypto/balance/{marketId}",method = RequestMethod.GET)
    public ResponseEntity<CryptoBalanceTrackQueryResponse> trackCryptoBalance(
            @PathVariable String marketId,
            @RequestBody CryptoBalanceTrackQuery cryptoBalanceTrackQuery) {
        cryptoBalanceTrackQuery.setMarketId(marketId);
        CryptoBalanceTrackQueryResponse response = portfolioApplicationService
                .trackCryptoBalance(cryptoBalanceTrackQuery);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }



    @Operation(
            summary = "원화 보유 수량 조회",
            description = "사용자의 원화(기초 통화) 보유 수량을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = CurrencyBalanceTrackQueryResponse.class)))
            }
    )
    @RequestMapping(path = "/currency/balance",method = RequestMethod.GET)
    public ResponseEntity<CurrencyBalanceTrackQueryResponse> trackCurrencyBalance(
            @RequestBody CurrencyBalanceTrackQuery currencyBalanceTrackQuery) {
        CurrencyBalanceTrackQueryResponse response = portfolioApplicationService
                .trackCurrencyBalance(currencyBalanceTrackQuery);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "사용자 포트폴리오 조회",
            description = "지정한 사용자의 전체 포트폴리오 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = PortfolioTrackQueryResponse.class)))
            }
    )
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<PortfolioTrackQueryResponse> trackPortfolio(
            @RequestHeader("X-header-User-Id") UUID tokenUserId) {
        PortfolioTrackQueryResponse response =
                portfolioApplicationService.trackPortfolio(new PortfolioTrackQuery(tokenUserId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "총 자산 평가 조회",
            description = "지정한 사용자의 전체 자산 총합 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = TotalBalanceTrackQueryResponse.class)))
            }
    )
    @RequestMapping(path = "/total/{portfolioId}",method = RequestMethod.GET)
    public ResponseEntity<TotalBalanceTrackQueryResponse> trackTotalBalances(
            @RequestHeader("X-header-User-Id") UUID tokenUserId,
            @PathVariable("portfolioId") UUID portfolioId) {
        TotalBalanceTrackQueryResponse response = portfolioApplicationService.trackTotalBalances(
                new TotalBalanceTrackQuery(portfolioId, tokenUserId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

//    @Operation(
//            summary = "포트폴리오 생성",
//            description = "사용자에게 새로운 포트폴리오를 생성합니다.",
//            responses = {
//                    @ApiResponse(responseCode = "201", description = "생성 성공",
//                            content = @Content(schema = @Schema(implementation = PortfolioCreatedResponse.class)))
//            }
//    )
//    @RequestMapping(path = "/portfolio",method = RequestMethod.POST)
//    public ResponseEntity<PortfolioCreatedResponse> createPortfolio(
//            @RequestHeader("X-header-User-Id") UUID tokenUserId) {
//        PortfolioCreatedResponse response = portfolioApplicationService
//                .createPortfolio(new PortfolioCreateCommand(tokenUserId));
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }

    @Operation(
            summary = "자산 변화 로그 조회",
            description = "지정한 사용자의 포트폴리오에서 자산 변화 로그를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = AssetChangLogTrackQueryResponse.class)))
            }
    )
    @RequestMapping(path = "/track/asset-log",method = RequestMethod.GET)
    public ResponseEntity<List<AssetChangLogTrackQueryResponse>>  trackAssetLog(
            @RequestHeader("X-header-User-Id") UUID tokenUserId) {
        List<AssetChangLogTrackQueryResponse> responses = portfolioApplicationService
                .trackAssetChangLog(new AssetChangLogTrackQuery(tokenUserId));
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

}
