package shop.shportfolio.user.database.jpa.mapper;

import shop.shportfolio.common.domain.valueobject.CreatedAt;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.user.database.jpa.entity.RoleEntity;
import shop.shportfolio.user.database.jpa.entity.SecuritySettingsEntity;
import shop.shportfolio.user.database.jpa.entity.TransactionHistoryEntity;
import shop.shportfolio.user.database.jpa.entity.UserEntity;
import shop.shportfolio.user.domain.entity.Role;
import shop.shportfolio.user.domain.entity.SecuritySettings;
import shop.shportfolio.user.domain.entity.TransactionHistory;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.*;

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
    public User userEntityToUser(UserEntity userEntity) {
        return User.builder()
                .userId(userEntity.getUserId())
                .email(userEntity.getEmail())
                .username(userEntity.getUsername())
                .password(userEntity.getEncodedPassword())
                .phoneNumber(userEntity.getPhoneNumber())
                .createdAt(userEntity.getCreatedAt())
                .roles(userEntity.getRoles().stream().map(this::roleEntityToRole).collect(Collectors.toList()))
                .profileImage(new ProfileImage(userEntity.getProfileImageId(),userEntity.getProfileImageExtensionWithName()
                        ,userEntity.getFileUrl()))
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

    public TransactionHistoryEntity transactionHistoryToTransactionHistoryEntity(
            TransactionHistory transactionHistory) {
        return new TransactionHistoryEntity(transactionHistory.getId().getValue(),
                transactionHistory.getUserId().getValue(),
                transactionHistory.getMarketId().getValue(),
                transactionHistory.getTransactionType(),
                transactionHistory.getAmount().getValue()
                , transactionHistory.getTransactionTime().getValue());
    }

    public TransactionHistory transactionHistoryEntityToTransactionHistory(
            TransactionHistoryEntity transactionHistoryEntity) {
        return TransactionHistory.builder()
                .transactionHistoryId(transactionHistoryEntity.getTransactionId())
                .userId(transactionHistoryEntity.getUserId())
                .marketId(transactionHistoryEntity.getMarketId())
                .transactionTime(transactionHistoryEntity.getTransactionTime())
                .amount(transactionHistoryEntity.getAmount())
                .transactionType(transactionHistoryEntity.getTransactionType())
                .build();
    }

}
