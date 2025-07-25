package shop.shportpolio.coupon.api.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import shop.shportfolio.common.domain.valueobject.RoleType;
import shop.shportfolio.coupon.api.CouponResource;
import shop.shportfolio.coupon.application.command.create.CouponCreateCommand;
import shop.shportfolio.coupon.application.command.create.CouponCreatedResponse;
import shop.shportfolio.coupon.application.command.track.CouponListTrackQuery;
import shop.shportfolio.coupon.application.command.track.CouponTrackQueryResponse;
import shop.shportfolio.coupon.application.command.track.CouponUsageTrackQueryResponse;
import shop.shportfolio.coupon.application.command.update.CouponCancelUpdateCommand;
import shop.shportfolio.coupon.application.command.update.CouponCancelUpdateResponse;
import shop.shportfolio.coupon.application.command.update.CouponUseUpdateCommand;
import shop.shportfolio.coupon.application.command.update.CouponUseUpdateResponse;
import shop.shportfolio.coupon.application.ports.input.CouponApplicationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

class CouponResourceTest {

    private CouponApplicationService couponApplicationService;
    private CouponResource couponResource;

    @BeforeEach
    void setUp() {
        couponApplicationService = Mockito.mock(CouponApplicationService.class);
        couponResource = new CouponResource(couponApplicationService);
    }

    @Test
    @DisplayName("쿠폰 생성하는 테스트")
    void createCoupon_ReturnsCreatedResponse() {
        // given
        UUID userId = UUID.randomUUID();
        List<String> roles = List.of("USER");
        CouponCreateCommand command = new CouponCreateCommand();

        CouponCreatedResponse expectedResponse = new CouponCreatedResponse(
                UUID.randomUUID(),
                userId,
                1000,
                LocalDate.now(ZoneOffset.UTC).plusDays(30),
                LocalDate.now(ZoneOffset.UTC),
                "COUPON123",
                shop.shportfoilo.coupon.domain.valueobject.CouponStatus.ACTIVE
        );

        Mockito.when(couponApplicationService.createCoupon(Mockito.any(CouponCreateCommand.class))).thenReturn(expectedResponse);

        // when
        ResponseEntity<CouponCreatedResponse> response = couponResource.createCoupon(command, userId, roles);

        // then
        assert response.getStatusCode() == HttpStatus.CREATED;
        assert response.getBody() != null;
        assert response.getBody().getCouponId().equals(expectedResponse.getCouponId());
        assert command.getUserId().equals(userId);
        assert command.getRoles().equals(List.of(RoleType.USER));
        Mockito.verify(couponApplicationService, Mockito.times(1)).createCoupon(command);
    }

    @Test
    @DisplayName("쿠폰 리스트를 조회하는 테스트")
    void retrieveCoupons_ReturnsCouponList() {
        // given
        UUID userId = UUID.randomUUID();

        CouponTrackQueryResponse mockCoupon = new CouponTrackQueryResponse(
                UUID.randomUUID(),
                userId,
                1000,
                LocalDate.now(ZoneOffset.UTC).plusDays(30),
                LocalDate.now(ZoneOffset.UTC),
                "COUPON123",
                shop.shportfoilo.coupon.domain.valueobject.CouponStatus.ACTIVE
        );
        List<CouponTrackQueryResponse> mockList = List.of(mockCoupon);

        Mockito.when(couponApplicationService.trackCouponList(Mockito.any(CouponListTrackQuery.class))).thenReturn(mockList);

        // when
        ResponseEntity<List<CouponTrackQueryResponse>> response = couponResource.retrieveCoupons(userId);

        // then
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody() != null && !response.getBody().isEmpty();
        assert response.getBody().get(0).getCouponId().equals(mockCoupon.getCouponId());
        Mockito.verify(couponApplicationService, Mockito.times(1)).trackCouponList(Mockito.any(CouponListTrackQuery.class));
    }

    @Test
    @DisplayName("쿠폰 사용하는 테스트")
    void useCoupon_ReturnsAcceptedResponse() {
        // given
        UUID userId = UUID.randomUUID();
        UUID couponId = UUID.randomUUID();
        CouponUseUpdateCommand command = new CouponUseUpdateCommand();

        CouponUseUpdateResponse expectedResponse = new CouponUseUpdateResponse(
                couponId,
                userId,
                1000,
                "COUPON123",
                shop.shportfoilo.coupon.domain.valueobject.CouponStatus.USED
        );

        Mockito.when(couponApplicationService.useCoupon(Mockito.any(CouponUseUpdateCommand.class))).thenReturn(expectedResponse);

        // when
        ResponseEntity<CouponUseUpdateResponse> response = couponResource.useCoupon(couponId, command, userId);

        // then
        assert response.getStatusCode() == HttpStatus.ACCEPTED;
        assert response.getBody() != null;
        assert response.getBody().getCouponId().equals(couponId);
        assert command.getUserId().equals(userId);
        assert command.getCouponId().equals(couponId);
        Mockito.verify(couponApplicationService, Mockito.times(1)).useCoupon(command);
    }

    @Test
    @DisplayName("쿠폰 취소하는 테스트")
    void cancelCoupon_ReturnsAcceptedResponse() {
        // given
        UUID userId = UUID.randomUUID();
        UUID couponId = UUID.randomUUID();
        CouponCancelUpdateCommand command = new CouponCancelUpdateCommand();

        CouponCancelUpdateResponse expectedResponse = new CouponCancelUpdateResponse(
                couponId,
                "사용자 요청 취소",
                shop.shportfoilo.coupon.domain.valueobject.CouponStatus.CANCELED,
                LocalDateTime.now(ZoneOffset.UTC)
        );

        Mockito.when(couponApplicationService.cancelCoupon(Mockito.any(CouponCancelUpdateCommand.class))).thenReturn(expectedResponse);

        // when
        ResponseEntity<CouponCancelUpdateResponse> response = couponResource.cancelCoupon(userId, couponId, command);

        // then
        assert response.getStatusCode() == HttpStatus.ACCEPTED;
        assert response.getBody() != null;
        assert response.getBody().getCouponId().equals(couponId);
        assert response.getBody().getCancelReason().equals("사용자 요청 취소");
        assert command.getUserId().equals(userId);
        assert command.getCouponId().equals(couponId);
        Mockito.verify(couponApplicationService, Mockito.times(1)).cancelCoupon(command);
    }

    @Test
    @DisplayName("쿠폰 사용 정보 조회 테스트")
    void trackCouponUsage_ReturnsUsageInfo() {
        // given
        UUID userId = UUID.randomUUID();
        UUID couponId = UUID.randomUUID();
        UUID couponUsageId = UUID.randomUUID();
        LocalDate issuedDate = LocalDate.now(ZoneOffset.UTC).minusDays(5);
        LocalDate expiryDate = LocalDate.now(ZoneOffset.UTC).plusDays(25);

        CouponUsageTrackQueryResponse expectedResponse = new CouponUsageTrackQueryResponse(
                couponId,
                couponUsageId,
                userId,
                expiryDate,
                issuedDate
        );

        Mockito.when(couponApplicationService.trackCouponUsage(Mockito.any()))
                .thenReturn(expectedResponse);

        // when
        ResponseEntity<CouponUsageTrackQueryResponse> response = couponResource.trackCouponUsage(userId, couponId);

        // then
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody() != null;
        assert response.getBody().getCouponId().equals(couponId);
        assert response.getBody().getCouponUsageId().equals(couponUsageId);
        assert response.getBody().getUserId().equals(userId);
        assert response.getBody().getIssuedDate().equals(issuedDate);
        assert response.getBody().getExpiryDate().equals(expiryDate);

        Mockito.verify(couponApplicationService, Mockito.times(1))
                .trackCouponUsage(Mockito.any());
    }

}
