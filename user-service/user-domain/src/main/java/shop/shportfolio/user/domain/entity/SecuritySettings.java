package shop.shportfolio.user.domain.entity;

import lombok.Builder;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.user.domain.valueobject.SecuritySettingsId;
import shop.shportfolio.user.domain.valueobject.TwoFactorAuthMethod;

public class SecuritySettings extends BaseEntity<SecuritySettingsId> {


    private TwoFactorAuthMethod twoFactorAuthMethod;
    private Boolean isEnabled;

    public SecuritySettings(SecuritySettingsId securitySettingsId) {

    }

    @Builder
    public SecuritySettings(SecuritySettingsId securitySettingsId, TwoFactorAuthMethod twoFactorAuthMethod, Boolean isEnabled) {
        this.twoFactorAuthMethod = twoFactorAuthMethod;
        this.isEnabled = isEnabled;
    }
}
