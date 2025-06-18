package shop.shportfolio.user.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.shportfolio.user.api.exception.UserNotAccessException;
import shop.shportfolio.user.application.command.auth.UserTempEmailAuthRequestCommand;
import shop.shportfolio.user.application.command.auth.UserTempEmailAuthVerifyCommand;
import shop.shportfolio.user.application.command.auth.VerifiedTempEmailUserResponse;
import shop.shportfolio.user.application.command.create.UserCreateCommand;
import shop.shportfolio.user.application.command.create.UserCreatedResponse;
import shop.shportfolio.user.application.command.delete.UserDeleteCommand;
import shop.shportfolio.user.application.command.track.*;
import shop.shportfolio.user.application.ports.input.TransactionHistoryApplicationService;
import shop.shportfolio.user.application.ports.input.UserApplicationService;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api")
public class UserCRDResources {

    private final UserApplicationService userApplicationService;
    private final TransactionHistoryApplicationService transactionHistoryApplicationService;

    @Autowired
    public UserCRDResources(UserApplicationService userApplicationService,
                            TransactionHistoryApplicationService transactionHistoryApplicationService) {
        this.userApplicationService = userApplicationService;
        this.transactionHistoryApplicationService = transactionHistoryApplicationService;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/users")
    public ResponseEntity<UserCreatedResponse> createUser(@RequestBody UserCreateCommand userCreateCommand) {
        UserCreatedResponse createdResponse = userApplicationService.createUser(userCreateCommand);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdResponse);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/emails")
    public ResponseEntity<Void> SendEmailTempCreateUser(@RequestBody UserTempEmailAuthRequestCommand
                                                                userTempEmailAuthRequestCommand) {
        userApplicationService.sendTempEmailCodeForCreateUser(userTempEmailAuthRequestCommand);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.POST, path = "/emails/confirm")
    public ResponseEntity<VerifiedTempEmailUserResponse> verifyUserEmailCode(@RequestBody UserTempEmailAuthVerifyCommand
                                                                                     userTempEmailAuthVerifyCommand) {
        VerifiedTempEmailUserResponse verifiedTempEmailUserResponse = userApplicationService
                .verifyTempEmailCodeForCreateUser(userTempEmailAuthVerifyCommand);
        return ResponseEntity.accepted().body(verifiedTempEmailUserResponse);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/users/{userId}")
    public ResponseEntity<TrackUserQueryResponse> retrieveUser(@RequestHeader("X-header-User-Id") UUID tokenUserId,
                                                               @PathVariable("userId") UUID userId) {
        isOwner(userId, tokenUserId);
        TrackUserQueryResponse trackUserQueryResponse = userApplicationService.trackUserQuery(new UserTrackQuery(userId));
        return ResponseEntity.ok().body(trackUserQueryResponse);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/users/{userId}")
    public ResponseEntity<Void> deleteUser(@RequestHeader("X-header-User-Id") UUID tokenUserId,
                                           @PathVariable("userId") UUID userId) {
        isOwner(userId, tokenUserId);
        userApplicationService.deleteUser(new UserDeleteCommand(userId));
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/users/{userId}/transactions")
    public ResponseEntity<TrackUserTrHistoryQueryResponse> retrieveUserTrHistories(
            @RequestHeader("X-header-User-Id") UUID tokenUserId, @PathVariable UUID userId) {
        isOwner(userId, tokenUserId);
        TrackUserTrHistoryQueryResponse transactionHistories = transactionHistoryApplicationService
                .findTransactionHistories(new UserTrHistoryListTrackQuery(tokenUserId));
        return ResponseEntity.ok().body(transactionHistories);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/users/{userId}/transactions/{transactionId}")
    public ResponseEntity<TrackUserTrHistoryQueryResponse> retrieveUserTrHistory(
            @RequestHeader("X-header-User-Id") UUID tokenUserId, @PathVariable UUID userId,
            @PathVariable UUID transactionId) {
        isOwner(userId, tokenUserId);
        TrackUserTrHistoryQueryResponse transactionHistories = transactionHistoryApplicationService
                .findOneTransactionHistory(new UserTrHistoryOneTrackQuery(userId, transactionId));
        return ResponseEntity.ok().body(transactionHistories);
    }


    private void isOwner(UUID userId, UUID ownerId) {
        if (!userId.equals(ownerId)) {
            throw new UserNotAccessException(String.format("%s is not owner of %s", userId, ownerId));
        }
    }
}
