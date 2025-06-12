package shop.shportfolio.user.domain.entity;

import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.user.domain.valueobject.RoleId;
import shop.shportfolio.user.domain.valueobject.RoleType;

public class Role extends BaseEntity<RoleId> {

    private RoleType roleType;


    public Role() {

    }

    public Role(RoleType roleType) {
        this.roleType = roleType;
    }


    public RoleType getRoleType() {
        return roleType;
    }


}
