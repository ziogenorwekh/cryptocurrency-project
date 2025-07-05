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
import shop.shportfolio.user.application.ports.output.mail.MailSenderPort;
import shop.shportfolio.user.application.ports.output.redis.RedisPort;
import shop.shportfolio.user.application.ports.output.repository.UserRepositoryPort;
import shop.shportfolio.user.application.ports.output.security.AuthenticatorPort;
import shop.shportfolio.user.application.ports.output.security.JwtTokenPort;
import shop.shportfolio.user.application.ports.output.s3.S3BucketPort;
import shop.shportfolio.user.application.ports.output.security.PasswordEncoderPort;
import shop.shportfolio.user.domain.UserDomainService;
import shop.shportfolio.user.domain.UserDomainServiceImpl;

@Configuration
public class TestUserApplicationMockBean {

    private final UserRepositoryPort userRepositoryPort = Mockito.mock(UserRepositoryPort.class);
    private final RedisPort redisPort = Mockito.mock(RedisPort.class);
    private final MailSenderPort mailSenderPort = Mockito.mock(MailSenderPort.class);
    private final AuthCodeGenerator authCodeGenerator = Mockito.mock(AuthCodeGenerator.class);
    private final JwtTokenPort jwtTokenPort = Mockito.mock(JwtTokenPort.class);
    private final S3BucketPort s3BucketPort = Mockito.mock(S3BucketPort.class);
    private final FileGenerator fileGenerator = Mockito.mock(FileGenerator.class);
    private final AuthenticatorPort authenticatorPort = Mockito.mock(AuthenticatorPort.class);

    @Bean
    public UserRepositoryPort userRepositoryAdapter() {
        return userRepositoryPort;
    }

    @Bean
    public RedisPort redisAdapter() {
        return redisPort;
    }

    @Bean
    public MailSenderPort mailSenderAdapter() {
        return mailSenderPort;
    }

    @Bean
    public AuthCodeGenerator authCodeGenerator() {
        return authCodeGenerator;
    }

    @Bean
    public JwtTokenPort jwtToken() {
        return jwtTokenPort;
    }

    @Bean
    public S3BucketPort s3BucketAdapter() {
        return s3BucketPort;
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
    public PasswordEncoderPort passwordEncoder() {
        return Mockito.mock(PasswordEncoderPort.class);
    }

    @Bean
    public UserCommandHandler userCommandHandler() {
        return new UserCommandHandler(userRepositoryPort, userDomainService(),passwordEncoder());
    }

    @Bean
    public UserQueryHandler userQueryHandler() {
        return new UserQueryHandler(userRepositoryPort);
    }

    @Bean
    public PasswordUpdateFacade passwordResetFacade() {
        return new PasswordUpdateFacade(jwtTokenPort, passwordEncoder(), userCommandHandler()
                , mailSenderAdapter());
    }

    @Bean
    public UserRegistrationFacade userRegistrationFacade() {
        return new UserRegistrationFacade(redisPort, authCodeGenerator, passwordEncoder(), userCommandHandler(), mailSenderPort);
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
                redisPort, userCommandHandler(), mailSenderPort, authCodeGenerator
        );
    }

    @Bean
    public UserUpdateDeleteUseCase userUpdateDeleteUseCase() {
        return new UserUpdateDeleteFacade(s3BucketPort, userCommandHandler(), fileGenerator);
    }

    @Bean
    public UserAuthenticationService userAuthenticationService() {
        return new UserAuthenticationServiceImpl(userAuthenticationUseCase(), userDataMapper());
    }

    @Bean
    public UserAuthenticationUseCase userAuthenticationUseCase() {
        return new UserAuthenticationFacade(authenticatorPort, mailSenderPort, userQueryHandler()
                , authCodeGenerator, redisPort);
    }

}
