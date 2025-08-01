package shop.shportfolio.user.api.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.shportfolio.user.api.exception.UserNotAccessException;
import shop.shportfolio.user.application.command.track.TrackUserTwoFactorResponse;
import shop.shportfolio.user.application.command.track.UserTwoFactorTrackQuery;
import shop.shportfolio.user.application.command.update.TwoFactorDisableCommand;
import shop.shportfolio.user.application.command.update.TwoFactorEmailVerifyCodeCommand;
import shop.shportfolio.user.application.command.update.TwoFactorEnableCommand;
import shop.shportfolio.user.application.ports.input.UserApplicationService;

import java.util.UUID;

@Tag(name = "User 2FA API", description = "사용자 2단계 인증(2FA) 관련 API")
@RestController
@RequestMapping(path = "/api")
public class User2FAResources {


    private final UserApplicationService userApplicationService;

    @Autowired
    public User2FAResources(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }

    @Operation(
            summary = "사용자 2FA 설정 상태 조회",
            description = "지정한 사용자의 2단계 인증 설정 상태를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = TrackUserTwoFactorResponse.class)))
            }
    )
    @RequestMapping(method = RequestMethod.GET, path = "/users/{userId}/security")
    public ResponseEntity<TrackUserTwoFactorResponse> trackUserTwoFactor(@RequestHeader("X-header-User-Id")
                                                                         UUID tokenUserId, @PathVariable UUID userId) {
        isOwner(userId, tokenUserId);
        TrackUserTwoFactorResponse trackUserTwoFactorResponse = userApplicationService
                .trackUserTwoFactorQuery(new UserTwoFactorTrackQuery(userId));
        return ResponseEntity.ok(trackUserTwoFactorResponse);
    }


    @Operation(
            summary = "사용자 2FA 설정 생성",
            description = "지정한 사용자에 대해 2단계 인증을 설정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "설정 성공")
            }
    )
    @RequestMapping(method = RequestMethod.POST, path = "/users/{userId}/security/setting")
    public ResponseEntity<Void> createUserTwoFactor(@RequestHeader("X-header-User-Id") UUID tokenUserId,
                                                    @PathVariable UUID userId,
                                                    @RequestBody TwoFactorEnableCommand twoFactorEnableCommand) {
        isOwner(userId, tokenUserId);
        twoFactorEnableCommand.setUserId(userId);
        userApplicationService.create2FASetting(twoFactorEnableCommand);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "사용자 2FA 설정 확인",
            description = "지정한 사용자의 2단계 인증 코드를 검증합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "검증 성공")
            }
    )
    @RequestMapping(method = RequestMethod.PATCH, path = "/users/{userId}/security/confirm")
    public ResponseEntity<Void> confirmUserTwoFactor(
            @RequestHeader("X-header-User-Id") UUID tokenUserId,
            @PathVariable UUID userId,
            @RequestBody TwoFactorEmailVerifyCodeCommand twoFactorEmailVerifyCodeCommand) {
        isOwner(userId, tokenUserId);
        userApplicationService.save2FA(twoFactorEmailVerifyCodeCommand);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "사용자 2FA 설정 해제",
            description = "지정한 사용자의 2단계 인증을 해제합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "해제 성공")
            }
    )
    @RequestMapping(method = RequestMethod.DELETE, path = "/users/{userId}/security")
    public ResponseEntity<TrackUserTwoFactorResponse> deleteUserTwoFactor(@RequestHeader("X-header-User-Id")
                                                                         UUID tokenUserId, @PathVariable UUID userId) {
        isOwner(userId, tokenUserId);
        userApplicationService.disableTwoFactorMethod(new TwoFactorDisableCommand(userId));
        return ResponseEntity.noContent().build();
    }

    private void isOwner(UUID userId, UUID ownerId) {
        if (!userId.equals(ownerId)) {
            throw new UserNotAccessException(String.format("%s is not owner of %s", userId, ownerId));
        }
    }
}