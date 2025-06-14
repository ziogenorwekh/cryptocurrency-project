package application.tmpbean;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import shop.shportfolio.user.application.ports.input.UserApplicationService;
import shop.shportfolio.user.application.UserApplicationServiceImpl;
import shop.shportfolio.user.application.PasswordResetFacade;
import shop.shportfolio.user.application.UserRegistrationFacade;
import shop.shportfolio.user.application.generator.AuthCodeGenerator;
import shop.shportfolio.user.application.generator.FileGenerator;
import shop.shportfolio.user.application.handler.UserCommandHandler;
import shop.shportfolio.user.application.handler.UserQueryHandler;
import shop.shportfolio.user.application.mapper.UserDataMapper;
import shop.shportfolio.user.application.ports.output.mail.MailSenderAdapter;
import shop.shportfolio.user.application.ports.output.redis.RedisAdapter;
import shop.shportfolio.user.application.ports.output.repository.UserRepositoryAdapter;
import shop.shportfolio.user.application.ports.output.jwt.JwtTokenAdapter;
import shop.shportfolio.user.application.ports.output.s3.S3BucketAdapter;
import shop.shportfolio.user.domain.UserDomainService;
import shop.shportfolio.user.domain.UserDomainServiceImpl;

@Configuration
public class TestUserApplicationMockBean {

    @Bean
    public UserDomainService userDomainService() {
        return new UserDomainServiceImpl();
    }

    @Bean
    public UserDataMapper userApplicationMapper() {
        return new UserDataMapper();
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
    public RedisAdapter cacheAdapter() {
        return Mockito.mock(RedisAdapter.class);
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
    public RedisAdapter redisAdapter() {
        return Mockito.mock(RedisAdapter.class);
    }

    @Bean
    public AuthCodeGenerator authCodeGenerator() {
        return Mockito.mock(AuthCodeGenerator.class);
    }
    @Bean
    public MailSenderAdapter mailSenderAdapter() {
        return Mockito.mock(MailSenderAdapter.class);
    }

    @Bean
    public JwtTokenAdapter jwtToken() {
        return Mockito.mock(JwtTokenAdapter.class);
    }
    @Bean
    public UserApplicationService userApplicationService() {
        return new UserApplicationServiceImpl(userCommandHandler(), userApplicationMapper()
                        , userQueryHandler(), passwordEncoder(),mailSenderAdapter(),s3BucketAdapter(),fileGenerator(),
                userRegistrationFacade(),passwordResetFacade());
    }

    @Bean
    public S3BucketAdapter s3BucketAdapter() {
        return Mockito.mock(S3BucketAdapter.class);
    }
    @Bean
    public PasswordResetFacade passwordResetFacade() {
        return new PasswordResetFacade(userQueryHandler(), jwtToken());
    }

    @Bean
    public UserRegistrationFacade userRegistrationFacade() {
        return new UserRegistrationFacade(redisAdapter(), authCodeGenerator());
    }

    @Bean
    public FileGenerator fileGenerator() {
        return Mockito.mock(FileGenerator.class);
    }

}
