package shop.shportfolio.user.api.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.shportfolio.common.exception.UserNotAccessException;
import shop.shportfolio.user.application.command.auth.UserTempEmailAuthRequestCommand;
import shop.shportfolio.user.application.command.auth.UserTempEmailAuthVerifyCommand;
import shop.shportfolio.user.application.command.auth.VerifiedTempEmailUserResponse;
import shop.shportfolio.user.application.command.create.UserCreateCommand;
import shop.shportfolio.user.application.command.create.UserCreatedResponse;
import shop.shportfolio.user.application.command.delete.UserDeleteCommand;
import shop.shportfolio.user.application.command.track.*;
import shop.shportfolio.user.application.ports.input.UserApplicationService;

import java.util.UUID;

@Tag(name = "User CRD API", description = "사용자 생성/조회/삭제 및 거래내역 조회 API")
@RestController
@RequestMapping(path = "/api")
public class UserCRDResources {

    private final UserApplicationService userApplicationService;

    @Autowired
    public UserCRDResources(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }

    @Operation(summary = "사용자 생성", description = "새로운 사용자를 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "생성 성공",
                            content = @Content(schema = @Schema(implementation = UserCreatedResponse.class)))
            })
    @RequestMapping(method = RequestMethod.POST, path = "/auth/users")
    public ResponseEntity<UserCreatedResponse> createUser(@RequestBody UserCreateCommand userCreateCommand) {
        // 만약에 실제 전화번호를 넣으면, 문제되니까 010-0000-0000 으로 바꿔서 넣기
        userCreateCommand.setPhoneNumber("010-0000-0000");

        UserCreatedResponse createdResponse = userApplicationService.createUser(userCreateCommand);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdResponse);
    }


    @Operation(summary = "이메일 인증 코드 전송", description = "회원가입을 위한 임시 이메일 인증 코드를 전송합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "전송 성공")
            })
    @RequestMapping(method = RequestMethod.POST, path = "/auth/emails")
    public ResponseEntity<Void> SendEmailTempCreateUser(@RequestBody UserTempEmailAuthRequestCommand
                                                                userTempEmailAuthRequestCommand) {
        userApplicationService.sendTempEmailCodeForCreateUser(userTempEmailAuthRequestCommand);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "이메일 인증 코드 확인", description = "임시 이메일 인증 코드를 검증합니다.",
            responses = {
                    @ApiResponse(responseCode = "202", description = "검증 성공",
                            content = @Content(schema = @Schema(implementation = VerifiedTempEmailUserResponse.class)))
            })
    @RequestMapping(method = RequestMethod.POST, path = "/auth/emails/confirm")
    public ResponseEntity<VerifiedTempEmailUserResponse> verifyUserEmailCode(@RequestBody UserTempEmailAuthVerifyCommand
                                                                                     userTempEmailAuthVerifyCommand) {
        VerifiedTempEmailUserResponse verifiedTempEmailUserResponse = userApplicationService
                .verifyTempEmailCodeForCreateUser(userTempEmailAuthVerifyCommand);
        return ResponseEntity.accepted().body(verifiedTempEmailUserResponse);
    }

    @Operation(summary = "사용자 정보 조회", description = "지정한 사용자의 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = TrackUserQueryResponse.class)))
            })
    @RequestMapping(method = RequestMethod.GET, path = "/users")
    public ResponseEntity<TrackUserQueryResponse> retrieveUser(@RequestHeader("X-header-User-Id") UUID tokenUserId) {
//        isOwner(userId, tokenUserId);
        TrackUserQueryResponse trackUserQueryResponse = userApplicationService.trackUserQuery(new UserTrackQuery(tokenUserId));
        return ResponseEntity.ok().body(trackUserQueryResponse);
    }

    @Operation(summary = "사용자 삭제", description = "지정한 사용자를 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "삭제 성공")
            })
    @RequestMapping(method = RequestMethod.DELETE, path = "/users")
    public ResponseEntity<Void> deleteUser(@RequestHeader("X-header-User-Id") UUID tokenUserId) {
//        isOwner(userId, tokenUserId);
        userApplicationService.deleteUser(new UserDeleteCommand(tokenUserId));
        return ResponseEntity.noContent().build();
    }


    private void isOwner(UUID userId, UUID ownerId) {
        if (!userId.equals(ownerId)) {
            throw new UserNotAccessException(String.format("%s is not owner of %s", userId, ownerId));
        }
    }
}
