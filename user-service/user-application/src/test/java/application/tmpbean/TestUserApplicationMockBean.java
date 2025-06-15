package application.tmpbean;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import shop.shportfolio.user.application.UserTwoFactorAuthenticationFacade;
import shop.shportfolio.user.application.ports.input.UserApplicationService;
import shop.shportfolio.user.application.UserApplicationServiceImpl;
import shop.shportfolio.user.application.PasswordResetFacade;
import shop.shportfolio.user.application.UserRegistrationFacade;
import shop.shportfolio.user.application.generator.AuthCodeGenerator;
import shop.shportfolio.user.application.generator.FileGenerator;
import shop.shportfolio.user.application.handler.UserCommandHandler;
import shop.shportfolio.user.application.handler.UserQueryHandler;
import shop.shportfolio.user.application.mapper.UserDataMapper;
import shop.shportfolio.user.application.ports.input.UserTwoFactorAuthenticationUseCase;
import shop.shportfolio.user.application.ports.output.mail.MailSenderAdapter;
import shop.shportfolio.user.application.ports.output.redis.RedisAdapter;
import shop.shportfolio.user.application.ports.output.repository.UserRepositoryAdaptor;
import shop.shportfolio.user.application.ports.output.jwt.JwtTokenAdapter;
import shop.shportfolio.user.application.ports.output.s3.S3BucketAdapter;
import shop.shportfolio.user.domain.UserDomainService;
import shop.shportfolio.user.domain.UserDomainServiceImpl;

@Configuration
public class TestUserApplicationMockBean {

    private final UserRepositoryAdaptor userRepositoryAdaptor = Mockito.mock(UserRepositoryAdaptor.class);
    private final RedisAdapter redisAdapter = Mockito.mock(RedisAdapter.class);
    private final MailSenderAdapter mailSenderAdapter = Mockito.mock(MailSenderAdapter.class);
    private final AuthCodeGenerator authCodeGenerator = Mockito.mock(AuthCodeGenerator.class);
    private final JwtTokenAdapter jwtTokenAdapter = Mockito.mock(JwtTokenAdapter.class);
    private final S3BucketAdapter s3BucketAdapter = Mockito.mock(S3BucketAdapter.class);
    private final FileGenerator fileGenerator = Mockito.mock(FileGenerator.class);

    @Bean
    public UserRepositoryAdaptor userRepositoryAdapter() {
        return userRepositoryAdaptor;
    }

    @Bean
    public RedisAdapter redisAdapter() {
        return redisAdapter;
    }

    @Bean
    public MailSenderAdapter mailSenderAdapter() {
        return mailSenderAdapter;
    }

    @Bean
    public AuthCodeGenerator authCodeGenerator() {
        return authCodeGenerator;
    }

    @Bean
    public JwtTokenAdapter jwtToken() {
        return jwtTokenAdapter;
    }

    @Bean
    public S3BucketAdapter s3BucketAdapter() {
        return s3BucketAdapter;
    }

    @Bean
    public FileGenerator fileGenerator() {
        return fileGenerator;
    }

    @Bean
    public UserDomainService userDomainService() {
        return new UserDomainServiceImpl();
    }

    @Bean
    public UserDataMapper userDataMapper() {
        return new UserDataMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserCommandHandler userCommandHandler() {
        return new UserCommandHandler(userRepositoryAdaptor, userDomainService());
    }

    @Bean
    public UserQueryHandler userQueryHandler() {
        return new UserQueryHandler(userRepositoryAdaptor);
    }

    @Bean
    public PasswordResetFacade passwordResetFacade() {
        return new PasswordResetFacade(userQueryHandler(), jwtTokenAdapter);
    }

    @Bean
    public UserRegistrationFacade userRegistrationFacade() {
        return new UserRegistrationFacade(redisAdapter, authCodeGenerator);
    }

    @Bean
    public UserApplicationService userApplicationService() {
        return new UserApplicationServiceImpl(
                userCommandHandler(),
                userDataMapper(),
                userQueryHandler(),
                passwordEncoder(),
                mailSenderAdapter,
                s3BucketAdapter,
                fileGenerator,
                userRegistrationFacade(),
                passwordResetFacade(),
                userTwoFactorAuthenticationUseCase()
        );
    }

    @Bean
    public UserTwoFactorAuthenticationUseCase userTwoFactorAuthenticationUseCase() {
        return new UserTwoFactorAuthenticationFacade(
                redisAdapter,userCommandHandler(),mailSenderAdapter,authCodeGenerator
        );
    }
}
