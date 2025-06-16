package shop.shportfolio.user.application.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.Token;
import shop.shportfolio.user.application.command.auth.UserTempEmailAuthenticationResponse;
import shop.shportfolio.user.application.command.auth.VerifiedTempEmailUserResponse;
import shop.shportfolio.user.application.command.create.UserCreatedResponse;
import shop.shportfolio.user.application.command.update.PwdUpdateTokenResponse;
import shop.shportfolio.user.application.command.track.TrackUserQueryResponse;
import shop.shportfolio.user.application.command.track.TrackUserTrHistoryQueryResponse;
import shop.shportfolio.user.application.dto.TransactionHistoryDTO;
import shop.shportfolio.user.application.command.update.UploadUserImageResponse;
import shop.shportfolio.user.domain.entity.TransactionHistory;
import shop.shportfolio.user.domain.entity.User;

import java.util.List;
import java.util.UUID;

@Component
public class UserDataMapper {

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

    public UserTempEmailAuthenticationResponse valueToUserTempEmailAuthenticationResponse(String code) {
        return UserTempEmailAuthenticationResponse.builder().code(code).build();
    }

    public VerifiedTempEmailUserResponse valueToVerifiedTempEmailUserResponse(UUID userId, String email) {

        return VerifiedTempEmailUserResponse.builder().userId(userId).email(email).build();
    }

    public TrackUserTrHistoryQueryResponse listToTrackUserTransactionHistoryQueryResponse(
            List<TransactionHistory> transactionHistoryList) {
        TrackUserTrHistoryQueryResponse trackUserTrHistoryQueryResponse =
                new TrackUserTrHistoryQueryResponse();
        transactionHistoryList.forEach(transactionHistory -> {
            trackUserTrHistoryQueryResponse.getTransactionHistoryList().add(
                    new TransactionHistoryDTO(transactionHistory.getMarketId().getValue(),
                            transactionHistory.getTransactionType().name()
                            , transactionHistory.getAmount().getValue().toString()
                            , transactionHistory.getTransactionTime().getValue())
            );
        });
        return trackUserTrHistoryQueryResponse;
    }

    public TrackUserTrHistoryQueryResponse transactionHistoryToTrackUserTransactionHistoryQueryResponse(
            TransactionHistory transactionHistory) {
        TrackUserTrHistoryQueryResponse trackUserTrHistoryQueryResponse = new TrackUserTrHistoryQueryResponse();
        trackUserTrHistoryQueryResponse.getTransactionHistoryList().add(new TransactionHistoryDTO(
                transactionHistory.getMarketId().getValue(), transactionHistory.getTransactionType().name()
                , transactionHistory.getAmount().getValue().toString()
                , transactionHistory.getTransactionTime().getValue()));
        return trackUserTrHistoryQueryResponse;
    }

    public PwdUpdateTokenResponse tokenToPwdUpdateTokenResponse(Token token) {
        return PwdUpdateTokenResponse.builder().token(token.getValue()).build();
    }

    public UploadUserImageResponse userToUploadUserImageResponse(User user) {
        return UploadUserImageResponse.builder()
                .fileName(user.getProfileImage().getProfileImageExtensionWithName())
                .fileUrl(user.getProfileImage().getFileUrl())
                .build();
    }
}
