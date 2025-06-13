package application.tmpbean;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import shop.shportfolio.user.application.UserApplicationService;
import shop.shportfolio.user.application.UserApplicationServiceImpl;
import shop.shportfolio.user.application.handler.UserCommandHandler;
import shop.shportfolio.user.application.handler.UserQueryHandler;
import shop.shportfolio.user.application.mapper.UserApplicationMapper;
import shop.shportfolio.user.application.ports.output.cache.CacheAdapter;
import shop.shportfolio.user.application.ports.output.repository.UserRepositoryAdapter;
import shop.shportfolio.user.domain.UserDomainService;
import shop.shportfolio.user.domain.UserDomainServiceImpl;

@Configuration
public class TestMockBean {

    @Bean
    public UserDomainService userDomainService() {
        return new UserDomainServiceImpl();
    }

    @Bean
    public UserApplicationMapper userApplicationMapper() {
        return new UserApplicationMapper();
    }

    @Bean
    public UserRepositoryAdapter userRepositoryAdapter() {
        return Mockito.mock(UserRepositoryAdapter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public CacheAdapter cacheAdapter() {
        return Mockito.mock(CacheAdapter.class);
    }
    @Bean
    public UserCommandHandler userCommandHandler() {
        return new UserCommandHandler(userRepositoryAdapter(),userDomainService());
    }

    @Bean
    public UserQueryHandler userQueryHandler() {
        return new UserQueryHandler(userRepositoryAdapter());
    }

    @Bean
    public UserApplicationService userApplicationService() {
        return new UserApplicationServiceImpl(userCommandHandler(), cacheAdapter(), userApplicationMapper()
                        , userQueryHandler(), passwordEncoder());
    }


}
