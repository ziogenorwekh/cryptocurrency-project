package shop.shportfolio.user.security.adapter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import shop.shportfolio.user.application.ports.output.security.JwtTokenAdapter;
import shop.shportfolio.user.domain.valueobject.Email;
import shop.shportfolio.user.domain.valueobject.Token;
import shop.shportfolio.user.domain.valueobject.TokenRequestType;

import java.util.Objects;
import java.util.UUID;


@Component
public class JwtTokenAdapterImpl implements JwtTokenAdapter {

    private final Environment env;

    public JwtTokenAdapterImpl(Environment env) {
        this.env = env;
    }


    @Override
    public Token createResetRequestPwdToken(String email, TokenRequestType tokenRequestType) {
        JWTVerifier jwtVerifier = JWT.require(
                Algorithm.HMAC256(Objects.requireNonNull(env.getProperty("jwt.token.secret"))))
                .acceptExpiresAt(
                        Long.parseLong(Objects.requireNonNull(env.getProperty("jwt.token.expiration"))))
                .withIssuer(email)
                .withSubject(tokenRequestType.name())
                .build();
        return new Token(jwtVerifier.toString());
    }

    @Override
    public Token createUpdatePasswordToken(UUID userId, TokenRequestType tokenRequestType) {
        JWTVerifier jwtVerifier = JWT.require(
                        Algorithm.HMAC256(Objects.requireNonNull(env.getProperty("jwt.token.secret"))))
                .acceptExpiresAt(
                        Long.parseLong(Objects.requireNonNull(env.getProperty("jwt.token.expiration"))))
                .withIssuer(userId.toString())
                .withSubject(tokenRequestType.name())
                .build();
        return new Token(jwtVerifier.toString());
    }

    @Override
    public Email verifyResetPwdToken(Token token) {
        String email = JWT.require(Algorithm.HMAC256(Objects.requireNonNull(env.getProperty("jwt.token.secret"))))
                .build().verify(token.getValue()).getIssuer();
        return new Email(email);
    }

    @Override
    public String getUserIdByUpdatePasswordToken(Token token) {
        return null;
    }
}
