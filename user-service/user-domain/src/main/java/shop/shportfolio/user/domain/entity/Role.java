package shop.shportfolio.user.domain.entity;

import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.user.domain.valueobject.RoleId;
import shop.shportfolio.user.domain.valueobject.RoleType;

// will be eager loading
public class Role extends BaseEntity<RoleId> {

    private RoleType roleType;


    public Role(RoleId roleId) {
        setId(roleId);
    }

    public void grantRole(RoleType roleType) {
        this.roleType = roleType;
    }

    public RoleType getRoleType() {
        return roleType;
    }


}
