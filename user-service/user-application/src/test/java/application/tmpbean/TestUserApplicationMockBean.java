package application.tmpbean;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.shportfolio.user.application.*;
import shop.shportfolio.user.application.facade.*;
import shop.shportfolio.user.application.ports.input.*;
import shop.shportfolio.user.application.generator.AuthCodeGenerator;
import shop.shportfolio.user.application.generator.FileGenerator;
import shop.shportfolio.user.application.handler.UserCommandHandler;
import shop.shportfolio.user.application.handler.UserQueryHandler;
import shop.shportfolio.user.application.mapper.UserDataMapper;
import shop.shportfolio.user.application.ports.output.mail.MailSenderAdapter;
import shop.shportfolio.user.application.ports.output.redis.RedisAdapter;
import shop.shportfolio.user.application.ports.output.repository.UserRepositoryAdaptor;
import shop.shportfolio.user.application.ports.output.security.AuthenticatorPort;
import shop.shportfolio.user.application.ports.output.security.JwtTokenAdapter;
import shop.shportfolio.user.application.ports.output.s3.S3BucketAdapter;
import shop.shportfolio.user.application.ports.output.security.PasswordEncoderAdapter;
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
    private final AuthenticatorPort authenticatorPort = Mockito.mock(AuthenticatorPort.class);

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
    public AuthenticatorPort authenticatorPort() {
        return authenticatorPort;
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
    public PasswordEncoderAdapter passwordEncoder() {
        return Mockito.mock(PasswordEncoderAdapter.class);
    }

    @Bean
    public UserCommandHandler userCommandHandler() {
        return new UserCommandHandler(userRepositoryAdaptor, userDomainService(),passwordEncoder());
    }

    @Bean
    public UserQueryHandler userQueryHandler() {
        return new UserQueryHandler(userRepositoryAdaptor);
    }

    @Bean
    public PasswordUpdateFacade passwordResetFacade() {
        return new PasswordUpdateFacade(jwtTokenAdapter, passwordEncoder(), userCommandHandler()
                , mailSenderAdapter());
    }

    @Bean
    public UserRegistrationFacade userRegistrationFacade() {
        return new UserRegistrationFacade(redisAdapter, authCodeGenerator, passwordEncoder(), userCommandHandler(), mailSenderAdapter);
    }

    @Bean
    public UserApplicationService userApplicationService() {
        return new UserApplicationServiceImpl(
                userDataMapper(),
                userTrackUseCase(),
                userUpdateDeleteUseCase(),
                userRegistrationFacade(),
                passwordResetFacade(),
                userTwoFactorAuthenticationUseCase()
        );
    }

    @Bean
    public UserTrackUseCase userTrackUseCase() {
        return new UserTrackUseCaseFacade(userQueryHandler());
    }

    @Bean
    public UserTwoFactorAuthenticationUseCase userTwoFactorAuthenticationUseCase() {
        return new UserTwoFactorAuthenticationFacade(
                redisAdapter, userCommandHandler(), mailSenderAdapter, authCodeGenerator
        );
    }

    @Bean
    public UserUpdateDeleteUseCase userUpdateDeleteUseCase() {
        return new UserUpdateDeleteFacade(s3BucketAdapter, userCommandHandler(), fileGenerator);
    }

    @Bean
    public UserAuthenticationService userAuthenticationService() {
        return new UserAuthenticationServiceImpl(userAuthenticationUseCase(), userDataMapper());
    }

    @Bean
    public UserAuthenticationUseCase userAuthenticationUseCase() {
        return new UserAuthenticationFacade(authenticatorPort, mailSenderAdapter, userQueryHandler()
                , authCodeGenerator, redisAdapter);
    }

}
