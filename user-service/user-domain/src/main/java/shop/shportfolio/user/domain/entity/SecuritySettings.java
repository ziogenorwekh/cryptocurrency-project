package shop.shportfolio.user.domain.entity;

import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.user.domain.exception.UserDomainException;
import shop.shportfolio.user.domain.valueobject.SecuritySettingsId;
import shop.shportfolio.user.domain.valueobject.TwoFactorAuthMethod;

// will be eager loading
@Getter
public class SecuritySettings extends BaseEntity<SecuritySettingsId> {


    private TwoFactorAuthMethod twoFactorAuthMethod;
    private Boolean isEnabled;

    public SecuritySettings(SecuritySettingsId securitySettingsId) {
        this.isEnabled = false;
    }


    protected void enable() {
        this.isEnabled = true;
    }

    protected void setTwoFactorAuthMethod(TwoFactorAuthMethod twoFactorAuthMethod) {
        if (!isEnabled) {
            throw new UserDomainException("Security settings is disabled.");
        }
        this.twoFactorAuthMethod = twoFactorAuthMethod;
    }

    protected void disable() {
        this.isEnabled = false;
        this.twoFactorAuthMethod = null;
    }

}
