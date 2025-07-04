package shop.shportfolio.user.infrastructure.database.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.shportfolio.user.domain.valueobject.TwoFactorAuthMethod;

import java.util.UUID;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SECURITY_SETTINGS_ENTITY")
public class SecuritySettingsEntity {

    @Id
    @Column(name = "SECURITY_SETTINGS_ID", unique = true, nullable = false,
            updatable = false, columnDefinition = "BINARY(16)")
    private UUID securitySettingsId;


    @OneToOne
    @JoinColumn(name = "USER_ID")
    private UserEntity user;

    @Column(name = "TWO_FACTOR_AUTH_METHOD")
    @Enumerated(EnumType.STRING)
    private TwoFactorAuthMethod twoFactorAuthMethod;
    @Column(name = "IS_ENABLED")
    private Boolean isEnabled;

}
