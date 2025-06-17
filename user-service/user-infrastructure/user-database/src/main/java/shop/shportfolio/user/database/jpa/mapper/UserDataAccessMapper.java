package shop.shportfolio.user.database.jpa.mapper;

import shop.shportfolio.user.database.jpa.entity.RoleEntity;
import shop.shportfolio.user.database.jpa.entity.SecuritySettingsEntity;
import shop.shportfolio.user.database.jpa.entity.UserEntity;
import shop.shportfolio.user.domain.entity.Role;
import shop.shportfolio.user.domain.entity.SecuritySettings;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.ProfileImage;
import shop.shportfolio.user.domain.valueobject.RoleId;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserDataAccessMapper {

    public UserEntity userToUserEntity(User user) {
        return UserEntity.builder()
                .userId(user.getId().getValue())
                .email(user.getEmail().getValue())
                .encodedPassword(user.getPassword().getValue())
                .username(user.getUsername().getValue())
                .roles(user.getRoles().stream().map(this::rolesToRoleEntity)
                        .collect(Collectors.toList()))
                .createdAt(user.getCreatedAt().getValue())
                .fileUrl(user.getProfileImage().getFileUrl())
                .profileImageId(user.getProfileImage().getValue())
                .profileImageExtensionWithName(user.getProfileImage().getProfileImageExtensionWithName())
                .securitySettingsEntity(securitySettingsToSecuritySettingsEntity(user.getSecuritySettings()))
                .phoneNumber(user.getPhoneNumber().getValue())
                .build();
    }

    public SecuritySettingsEntity securitySettingsToSecuritySettingsEntity(SecuritySettings securitySettings) {
        return SecuritySettingsEntity.builder()
                .securitySettingsId(securitySettings.getId().getValue())
                .isEnabled(securitySettings.getIsEnabled())
                .twoFactorAuthMethod(securitySettings.getTwoFactorAuthMethod())
                .build();
    }

    public RoleEntity rolesToRoleEntity(Role role) {
        return RoleEntity.builder()
                .roleId(role.getId().getValue())
                .roleType(role.getRoleType())
                .build();
    }

    public User userEntityToUser(UserEntity userEntity) {
        return null;
    }

    public Role roleEntityToRole(RoleEntity roleEntity) {
        return new Role(roleEntity.getRoleId(),
                roleEntity.getRoleType());
    }

    public SecuritySettings securitySettingsEntityToSecuritySettings(
            SecuritySettingsEntity securitySettingsEntity) {
        return new SecuritySettings(securitySettingsEntity.getSecuritySettingsId(),
                securitySettingsEntity.getTwoFactorAuthMethod() == null ?
                        null : securitySettingsEntity.getTwoFactorAuthMethod()
                ,securitySettingsEntity.getIsEnabled());
    }


}
