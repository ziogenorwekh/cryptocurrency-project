package shop.shportfolio.user.security.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import shop.shportfolio.user.application.ports.output.repository.UserRepositoryAdaptor;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.security.model.CustomUserDetails;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepositoryAdaptor userRepositoryAdaptor;

    @Autowired
    public CustomUserDetailsService(UserRepositoryAdaptor userRepositoryAdaptor) {
        this.userRepositoryAdaptor = userRepositoryAdaptor;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepositoryAdaptor.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException(String.format("%s is not found", email)));
        return new CustomUserDetails(user);
    }
}
