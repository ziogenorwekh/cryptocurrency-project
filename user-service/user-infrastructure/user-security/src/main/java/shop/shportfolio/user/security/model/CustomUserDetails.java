package shop.shportfolio.user.security.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import shop.shportfolio.user.domain.entity.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().stream().map(role -> authorities.add(new SimpleGrantedAuthority(role.getRoleType().name())));
        return authorities;
    }

    public UUID getUserId() {
        return user.getId().getValue();
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return user.getPassword().getValue();
    }

    public String getEmail() {
        return user.getEmail().getValue();
    }

    @Override
    public String getUsername() {
        return user.getUsername().getValue();
    }

    public Boolean is2FAEnabled() {
        return user.getSecuritySettings().getIsEnabled();
    }
}
