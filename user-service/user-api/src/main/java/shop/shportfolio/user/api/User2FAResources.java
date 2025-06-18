package shop.shportfolio.user.api;

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

@RestController
@RequestMapping(path = "/api")
public class User2FAResources {


    private final UserApplicationService userApplicationService;

    @Autowired
    public User2FAResources(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/users/{userId}/security")
    public ResponseEntity<TrackUserTwoFactorResponse> trackUserTwoFactor(@RequestHeader("X-header-User-Id")
                                                                         UUID tokenUserId, @PathVariable UUID userId) {
        isOwner(userId, tokenUserId);
        TrackUserTwoFactorResponse trackUserTwoFactorResponse = userApplicationService
                .trackUserTwoFactorQuery(new UserTwoFactorTrackQuery(userId));
        return ResponseEntity.ok(trackUserTwoFactorResponse);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/users/{userId}/security/setting")
    public ResponseEntity<Void> createUserTwoFactor(@RequestHeader("X-header-User-Id") UUID tokenUserId,
                                                    @PathVariable UUID userId,
                                                    @RequestBody TwoFactorEnableCommand twoFactorEnableCommand) {
        isOwner(userId, tokenUserId);
        twoFactorEnableCommand.setUserId(userId);
        userApplicationService.create2FASetting(twoFactorEnableCommand);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.PATCH, path = "/users/{userId}/security/confirm")
    public ResponseEntity<Void> confirmUserTwoFactor(
            @RequestHeader("X-header-User-Id") UUID tokenUserId,
            @PathVariable UUID userId,
            @RequestBody TwoFactorEmailVerifyCodeCommand twoFactorEmailVerifyCodeCommand) {
        isOwner(userId, tokenUserId);
        userApplicationService.save2FA(twoFactorEmailVerifyCodeCommand);
        return ResponseEntity.noContent().build();
    }

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