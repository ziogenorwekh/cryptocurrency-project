package shop.shportfolio.user.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.shportfolio.user.application.command.auth.UserTempEmailAuthRequestCommand;
import shop.shportfolio.user.application.command.auth.UserTempEmailAuthVerifyCommand;
import shop.shportfolio.user.application.command.auth.VerifiedTempEmailUserResponse;
import shop.shportfolio.user.application.command.create.UserCreateCommand;
import shop.shportfolio.user.application.command.create.UserCreatedResponse;
import shop.shportfolio.user.application.command.delete.UserDeleteCommand;
import shop.shportfolio.user.application.command.track.TrackUserQueryResponse;
import shop.shportfolio.user.application.command.track.UserTrackQuery;
import shop.shportfolio.user.application.ports.input.UserApplicationService;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api")
public class UserCRDResources {

    private final UserApplicationService userApplicationService;

    @Autowired
    public UserCRDResources(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
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

    @RequestMapping(method = RequestMethod.POST,path = "/emails/confirm")
    public ResponseEntity<VerifiedTempEmailUserResponse> verifyUserEmailCode(@RequestBody UserTempEmailAuthVerifyCommand
                                                    userTempEmailAuthVerifyCommand) {
        VerifiedTempEmailUserResponse verifiedTempEmailUserResponse = userApplicationService
                .verifyTempEmailCodeForCreateUser(userTempEmailAuthVerifyCommand);
        return ResponseEntity.accepted().body(verifiedTempEmailUserResponse);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/users/{userId}")
    public ResponseEntity<TrackUserQueryResponse> retrieveUser(@PathVariable("userId")UUID userId) {
        TrackUserQueryResponse trackUserQueryResponse = userApplicationService.trackUserQuery(new  UserTrackQuery(userId));
        return ResponseEntity.ok().body(trackUserQueryResponse);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") UUID userId) {
        userApplicationService.deleteUser(new UserDeleteCommand(userId));
        return ResponseEntity.noContent().build();
    }

}
