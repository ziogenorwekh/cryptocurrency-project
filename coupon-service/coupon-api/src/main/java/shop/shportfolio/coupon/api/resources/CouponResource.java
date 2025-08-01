package shop.shportfolio.coupon.api.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.shportfolio.common.domain.valueobject.RoleType;
import shop.shportfolio.coupon.application.command.create.CouponCreateCommand;
import shop.shportfolio.coupon.application.command.create.CouponCreatedResponse;
import shop.shportfolio.coupon.application.command.track.*;
import shop.shportfolio.coupon.application.command.update.CouponCancelUpdateCommand;
import shop.shportfolio.coupon.application.command.update.CouponCancelUpdateResponse;
import shop.shportfolio.coupon.application.command.update.CouponUseUpdateCommand;
import shop.shportfolio.coupon.application.command.update.CouponUseUpdateResponse;
import shop.shportfolio.coupon.application.ports.input.CouponApplicationService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Coupon API 컨트롤러
 * <p>
 * 쿠폰 관련 REST API를 제공합니다.
 * </p>
 */
@Tag(name = "Coupon API", description = "쿠폰 관련 API")
@RestController
@RequestMapping(path = "/api")
public class CouponResource {

    private final CouponApplicationService couponApplicationService;


    @Autowired
    public CouponResource(CouponApplicationService couponApplicationService) {
        this.couponApplicationService = couponApplicationService;
    }

    /**
     * 쿠폰 생성 API
     *
     * @param command   쿠폰 생성 명령 객체
     * @param userId    요청 헤더의 사용자 ID
     * @param userRoles 요청 헤더의 사용자 역할 목록
     * @return 생성된 쿠폰 응답과 HTTP 201 상태코드 반환
     */
    @Operation(
            summary = "쿠폰 생성",
            description = "사용자 쿠폰을 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "쿠폰 생성 성공",
                            content = @Content(schema = @Schema(implementation = CouponCreatedResponse.class)))
            }
    )
    @RequestMapping(method = RequestMethod.POST, path = "/coupons")
    public ResponseEntity<CouponCreatedResponse> createCoupon(
            @RequestBody CouponCreateCommand command,
            @RequestHeader("X-header-User-Id") UUID userId,
            @RequestHeader("X-header-User-Roles") List<String> userRoles) {
        command.setUserId(userId);
        command.setRoles(userRoles.stream().map(RoleType::valueOf).collect(Collectors.toList()));
        CouponCreatedResponse couponCreatedResponse = couponApplicationService.createCoupon(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(couponCreatedResponse);
    }

    /**
     * 쿠폰 목록 조회 API
     *
     * @param userId 요청 헤더의 사용자 ID
     * @return 사용자 쿠폰 목록과 HTTP 200 상태코드 반환
     */
    @Operation(
            summary = "쿠폰 목록 조회",
            description = "사용자의 쿠폰 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = CouponTrackQueryResponse.class)))
            }
    )
    @RequestMapping(method = RequestMethod.GET, path = "/coupons")
    public ResponseEntity<List<CouponTrackQueryResponse>> retrieveCoupons(
            @RequestHeader("X-header-User-Id") UUID userId) {
        List<CouponTrackQueryResponse> couponTrackQueryResponses = couponApplicationService
                .trackCouponList(new CouponListTrackQuery(userId));
        return ResponseEntity.ok(couponTrackQueryResponses);
    }


    /**
     * 쿠폰 상세 조회 API
     *
     * @param couponId 쿠폰 ID (경로 변수)
     * @param userId   요청 헤더의 사용자 ID
     * @return 특정 쿠폰 상세 정보와 HTTP 200 상태코드 반환
     */
    @Operation(
            summary = "쿠폰 상세 조회",
            description = "특정 쿠폰의 상세 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = CouponTrackQueryResponse.class)))
            }
    )
    @RequestMapping(method = RequestMethod.GET, path = "/coupons/{couponId}")
    public ResponseEntity<CouponTrackQueryResponse> retrieveCoupon(
            @PathVariable("couponId") UUID couponId,
            @RequestHeader("X-header-User-Id") UUID userId) {
        CouponTrackQueryResponse couponTrackQueryResponse = couponApplicationService
                .trackCoupon(new CouponTrackQuery(userId, couponId));
        return ResponseEntity.ok(couponTrackQueryResponse);
    }

    /**
     * 쿠폰 사용 처리 API
     *
     * @param couponId 쿠폰 ID (경로 변수)
     * @param command  쿠폰 사용 명령 객체
     * @param userId   요청 헤더의 사용자 ID
     * @return 쿠폰 사용 처리 결과와 HTTP 202 상태코드 반환
     */
    @Operation(
            summary = "쿠폰 사용 처리",
            description = "쿠폰을 사용 처리합니다.",
            responses = {
                    @ApiResponse(responseCode = "202", description = "사용 처리 성공",
                            content = @Content(schema = @Schema(implementation = CouponUseUpdateResponse.class)))
            }
    )
    @RequestMapping(method = RequestMethod.PUT, path = "/coupons/{couponId}")
    public ResponseEntity<CouponUseUpdateResponse> useCoupon(
            @PathVariable("couponId") UUID couponId,
            @RequestBody CouponUseUpdateCommand command,
            @RequestHeader("X-header-User-Id") UUID userId) {

        command.setUserId(userId);
        command.setCouponId(couponId);
        CouponUseUpdateResponse couponUseUpdateResponse = couponApplicationService.useCoupon(command);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(couponUseUpdateResponse);
    }


    /**
     * 결제 정보 조회 API
     *
     * @param paymentId 결제 ID (경로 변수)
     * @param userId    요청 헤더의 사용자 ID
     * @return 특정 결제 정보와 HTTP 200 상태코드 반환
     */
    @Operation(
            summary = "결제 정보 조회",
            description = "특정 결제 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = PaymentTrackQueryResponse.class)))
            }
    )
    @RequestMapping(method = RequestMethod.GET, path = "/coupons/{paymentId}")
    public ResponseEntity<PaymentTrackQueryResponse> retrievePayment(
            @PathVariable UUID paymentId,
            @RequestHeader("X-header-User-Id") UUID userId) {
        PaymentTrackQueryResponse paymentTrackQueryResponse = couponApplicationService
                .trackPayment(new PaymentTrackQuery(userId, paymentId));
        return ResponseEntity.ok(paymentTrackQueryResponse);
    }


    /**
     * 쿠폰 취소 처리 API
     *
     * @param userId   요청 헤더의 사용자 ID
     * @param couponId 쿠폰 ID (경로 변수)
     * @param command  쿠폰 취소 명령 객체
     * @return 쿠폰 취소 처리 결과와 HTTP 202 상태코드 반환
     */
    @Operation(
            summary = "쿠폰 취소 처리",
            description = "쿠폰을 취소 처리합니다.",
            responses = {
                    @ApiResponse(responseCode = "202", description = "취소 처리 성공",
                            content = @Content(schema = @Schema(implementation = CouponCancelUpdateResponse.class)))
            }
    )
    @RequestMapping(method = RequestMethod.PUT, path = "/coupons/{couponId}")
    public ResponseEntity<CouponCancelUpdateResponse> cancelCoupon(
            @RequestHeader("X-header-User-Id") UUID userId,
            @PathVariable UUID couponId,
            @RequestBody CouponCancelUpdateCommand command
    ) {
        command.setUserId(userId);
        command.setCouponId(couponId);
        CouponCancelUpdateResponse couponCancelUpdateResponse = couponApplicationService.cancelCoupon(command);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(couponCancelUpdateResponse);
    }


    /**
     * 쿠폰 사용 정보 조회 API
     *
     * @param userId   요청 헤더의 사용자 ID
     * @param couponId 쿠폰 ID (경로 변수)
     * @return 쿠폰 사용 정보와 HTTP 200 상태코드 반환
     */
    @Operation(
            summary = "쿠폰 사용 정보 조회",
            description = "쿠폰을 사용하면 사용 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "상세 조회 성공",
                            content = @Content(schema = @Schema(implementation = CouponUsageTrackQueryResponse.class)))
            }
    )
    @RequestMapping(method = RequestMethod.GET, path = "/coupons/couponUsage/{couponId}")
    public ResponseEntity<CouponUsageTrackQueryResponse> trackCouponUsage(@RequestHeader("X-header-User-Id") UUID userId,
                                                                          @PathVariable UUID couponId) {
        CouponUsageTrackQueryResponse couponUsageTrackQueryResponse = couponApplicationService
                .trackCouponUsage(new CouponUsageTrackQuery(userId, couponId));
        return ResponseEntity.ok(couponUsageTrackQueryResponse);
    }
}
