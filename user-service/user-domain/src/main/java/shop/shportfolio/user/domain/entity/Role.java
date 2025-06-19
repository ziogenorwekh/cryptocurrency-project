package shop.shportfolio.user.domain.entity;

import lombok.Builder;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.user.domain.valueobject.RoleId;
import shop.shportfolio.user.domain.valueobject.RoleType;

import java.util.UUID;

// will be eager loading
public class Role extends BaseEntity<RoleId> {

    private RoleType roleType;


    public Role(RoleId roleId) {
        setId(roleId);
    }

    public Role(UUID roleId, RoleType roleType) {
        setId(new RoleId(roleId));
        this.roleType = roleType;
    }

    protected void grantRole(RoleType roleType) {
        this.roleType = roleType;
    }

    public RoleType getRoleType() {
        return roleType;
    }

}
