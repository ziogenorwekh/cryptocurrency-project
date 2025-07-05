package shop.shportfolio.user.infrastructure.database.jpa.mapper;

import shop.shportfolio.user.domain.entity.Role;
import shop.shportfolio.user.domain.entity.SecuritySettings;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.*;
import shop.shportfolio.user.infrastructure.database.jpa.entity.*;

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
                .profileImageEmbedded(ProfileImageEmbedded.builder()
                        .profileImageId(user.getProfileImage().getValue())
                        .fileUrl(user.getProfileImage().getFileUrl())
                        .profileImageExtensionWithName(user.getProfileImage().
                                getProfileImageExtensionWithName()).build())
                .securitySettingsEntity(securitySettingsToSecuritySettingsEntity(user.getSecuritySettings()))
                .phoneNumber(user.getPhoneNumber().getValue())
                .build();
    }
    public User userEntityToUser(UserEntity userEntity) {
        return User.builder()
                .userId(userEntity.getUserId())
                .email(userEntity.getEmail())
                .username(userEntity.getUsername())
                .password(userEntity.getEncodedPassword())
                .phoneNumber(userEntity.getPhoneNumber())
                .createdAt(userEntity.getCreatedAt())
                .roles(userEntity.getRoles().stream().map(this::roleEntityToRole).collect(Collectors.toList()))
                .profileImage(ProfileImage.builder()
                        .value(userEntity.getProfileImageEmbedded().getProfileImageId())
                        .fileUrl(userEntity.getProfileImageEmbedded().getFileUrl())
                        .profileImageExtensionWithName(userEntity.getProfileImageEmbedded().getProfileImageExtensionWithName())
                        .build())
                .securitySettings(this.securitySettingsEntityToSecuritySettings(userEntity.getSecuritySettingsEntity()))
                .build();
    }

    public SecuritySettingsEntity securitySettingsToSecuritySettingsEntity(SecuritySettings securitySettings) {
        return SecuritySettingsEntity.builder()
                .securitySettingsId(securitySettings.getId().getValue())
                .isEnabled(securitySettings.getIsEnabled())
                .twoFactorAuthMethod(securitySettings.getTwoFactorAuthMethod())
                .build();
    }
    public SecuritySettings securitySettingsEntityToSecuritySettings(
            SecuritySettingsEntity securitySettingsEntity) {
        return new SecuritySettings(securitySettingsEntity.getSecuritySettingsId(),
                securitySettingsEntity.getTwoFactorAuthMethod() == null ?
                        null : securitySettingsEntity.getTwoFactorAuthMethod()
                ,securitySettingsEntity.getIsEnabled());
    }

    public RoleEntity rolesToRoleEntity(Role role) {
        return RoleEntity.builder()
                .roleId(role.getId().getValue())
                .roleType(role.getRoleType())
                .build();
    }

    public Role roleEntityToRole(RoleEntity roleEntity) {
        return new Role(roleEntity.getRoleId(),
                roleEntity.getRoleType());
    }
}
