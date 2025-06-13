package shop.shportfolio.user.application.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.user.application.command.create.UserCreatedResponse;
import shop.shportfolio.user.application.command.track.TrackUserQueryResponse;
import shop.shportfolio.user.domain.entity.Role;
import shop.shportfolio.user.domain.entity.User;

@Component
public class UserApplicationMapper {

    public UserCreatedResponse userEntityToUserCreatedResponse(User user) {

        return UserCreatedResponse.builder()
                .userId(user.getId().getValue().toString())
                .email(user.getEmail().getValue())
                .phoneNumber(user.getPhoneNumber().getValue())
                .is2FAEnabled(user.getSecuritySettings().getIsEnabled())
                .createdAt(user.getCreatedAt().getValue())
                .roles(user.getRoles().stream().map(role -> role.getRoleType().toString()).toList())
                .twoFactorAuthMethod(user.getSecuritySettings().getTwoFactorAuthMethod() == null ? "" :
                        user.getSecuritySettings().getTwoFactorAuthMethod().toString())
                .username(user.getUsername().getValue())
                .build();
    }


    public TrackUserQueryResponse userEntityToUserTrackUserQueryResponse(User user) {
        return TrackUserQueryResponse.builder()
                .userId(user.getId().getValue().toString())
                .email(user.getEmail().getValue())
                .phoneNumber(user.getPhoneNumber().getValue())
                .is2FAEnabled(user.getSecuritySettings().getIsEnabled())
                .createdAt(user.getCreatedAt().getValue())
                .roles(user.getRoles().stream().map(role -> role.getRoleType().toString()).toList())
                .twoFactorAuthMethod(user.getSecuritySettings().getTwoFactorAuthMethod() == null ? "" :
                        user.getSecuritySettings().getTwoFactorAuthMethod().toString())
                .username(user.getUsername().getValue())
                .build();
    }

}
