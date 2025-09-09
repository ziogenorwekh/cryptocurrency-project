package shop.shportfolio.user.infrastructure.database.jpa.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.user.domain.entity.Role;
import shop.shportfolio.user.domain.entity.SecuritySettings;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.*;
import shop.shportfolio.user.infrastructure.database.jpa.entity.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserDataAccessMapper {

    // Domain -> JPA Entity
    public UserEntity userToUserEntity(User user) {
        // ProfileImage 먼저
        ProfileImageEmbedded profileImageEmbedded = ProfileImageEmbedded.builder()
                .profileImageId(user.getProfileImage().getValue())
                .fileUrl(user.getProfileImage().getFileUrl())
                .profileImageExtensionWithName(user.getProfileImage().getProfileImageExtensionWithName())
                .build();

        // UserEntity 생성 (roles/security는 builder로 나중에 추가)
        UserEntity userEntity = UserEntity.builder()
                .userId(user.getId().getValue())
                .email(user.getEmail().getValue())
                .username(user.getUsername().getValue())
                .encodedPassword(user.getPassword().getValue())
                .phoneNumber(user.getPhoneNumber().getValue())
                .createdAt(user.getCreatedAt().getValue())
                .profileImageEmbedded(profileImageEmbedded)
                .build();

        // RoleEntity 매핑 (user 포함 생성)
        List<RoleEntity> roleEntities = user.getRoles().stream()
                .map(role -> new RoleEntity(role.getId().getValue(), userEntity, role.getRoleType()))
                .collect(Collectors.toList());
        userEntity.getRoles().addAll(roleEntities);

        // SecuritySettings 매핑 (user 포함 생성)
        SecuritySettings security = user.getSecuritySettings();
        SecuritySettingsEntity securityEntity = new SecuritySettingsEntity(
                security.getId().getValue(),
                userEntity,
                security.getTwoFactorAuthMethod(),
                security.getIsEnabled()
        );
        userEntity.setSecuritySettingsEntity(securityEntity);

        return userEntity;
    }

    // JPA Entity -> Domain
    public User userEntityToUser(UserEntity userEntity) {
        // Roles
        List<Role> roles = userEntity.getRoles().stream()
                .map(re -> new Role(re.getRoleId(), re.getRoleType()))
                .collect(Collectors.toList());

        // ProfileImage
        ProfileImage profileImage = new ProfileImage(
                userEntity.getProfileImageEmbedded().getProfileImageId(),
                userEntity.getProfileImageEmbedded().getFileUrl(),
                userEntity.getProfileImageEmbedded().getProfileImageExtensionWithName()
        );

        // SecuritySettings
        SecuritySettingsEntity se = userEntity.getSecuritySettingsEntity();
        SecuritySettings securitySettings = new SecuritySettings(
                se.getSecuritySettingsId(),
                se.getTwoFactorAuthMethod(),
                se.getIsEnabled()
        );

        return User.builder()
                .userId(userEntity.getUserId())
                .email(userEntity.getEmail())
                .username(userEntity.getUsername())
                .password(userEntity.getEncodedPassword())
                .phoneNumber(userEntity.getPhoneNumber())
                .createdAt(userEntity.getCreatedAt())
                .roles(roles)
                .profileImage(profileImage)
                .securitySettings(securitySettings)
                .build();
    }
}
