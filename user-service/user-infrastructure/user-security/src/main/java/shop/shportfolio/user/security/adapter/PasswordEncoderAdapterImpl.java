package shop.shportfolio.user.security.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import shop.shportfolio.user.application.ports.output.security.PasswordEncoderAdapter;

@Component
public class PasswordEncoderAdapterImpl implements PasswordEncoderAdapter {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordEncoderAdapterImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return false;
    }

    @Override
    public String encode(String rawPassword) {
        return "";
    }
}
