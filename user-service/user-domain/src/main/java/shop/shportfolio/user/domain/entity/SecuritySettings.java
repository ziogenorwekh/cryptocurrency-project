package shop.shportfolio.user.domain.entity;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.user.domain.exception.UserDomainException;
import shop.shportfolio.user.domain.valueobject.SecuritySettingsId;
import shop.shportfolio.user.domain.valueobject.TwoFactorAuthMethod;

import java.util.UUID;

// will be eager loading
@Getter
public class SecuritySettings extends BaseEntity<SecuritySettingsId> {


    private TwoFactorAuthMethod twoFactorAuthMethod;
    private Boolean isEnabled;

    public SecuritySettings(SecuritySettingsId securitySettingsId) {
        setId(securitySettingsId);
        this.isEnabled = false;
    }

    @Builder
    public SecuritySettings(UUID securitySettingsId, TwoFactorAuthMethod twoFactorAuthMethod, Boolean isEnabled) {
        setId(new  SecuritySettingsId(securitySettingsId));
        this.twoFactorAuthMethod = twoFactorAuthMethod;
        this.isEnabled = isEnabled;
    }

    protected void enable() {
        if (isEnabled) {
            throw new UserDomainException("Security settings is already enabled 2FA setting");
        }
        if (twoFactorAuthMethod == null) {
            throw new UserDomainException("Two-factor authentication method is not set");
        }
        this.isEnabled = true;
    }

    protected void setTwoFactorAuthMethod(TwoFactorAuthMethod twoFactorAuthMethod) {
        this.twoFactorAuthMethod = twoFactorAuthMethod;
    }

    protected void disable() {
        if (!isEnabled) {
            throw new UserDomainException("Security settings is already disabled");
        }
        this.isEnabled = false;
        this.twoFactorAuthMethod = null;
    }

}
