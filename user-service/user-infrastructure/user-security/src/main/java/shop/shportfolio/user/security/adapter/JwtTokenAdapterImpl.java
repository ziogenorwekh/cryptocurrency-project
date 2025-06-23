package shop.shportfolio.user.security.adapter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import shop.shportfolio.user.application.exception.security.CustomJWTVerificationException;
import shop.shportfolio.user.application.exception.security.CustomTokenExpiredException;
import shop.shportfolio.user.application.ports.output.security.JwtTokenAdapter;
import shop.shportfolio.user.domain.valueobject.Token;
import shop.shportfolio.common.domain.valueobject.TokenType;
import shop.shportfolio.user.application.exception.security.TokenRequestTypeException;

import java.util.Objects;
import java.util.UUID;


@Component
public class JwtTokenAdapterImpl implements JwtTokenAdapter {

    private final Environment env;

    public JwtTokenAdapterImpl(Environment env) {
        this.env = env;
    }


    @Override
    public String generateResetTokenByEmail(String email, TokenType tokenType) {
        JWTVerifier jwtVerifier = JWT.require(
                        Algorithm.HMAC256(Objects.requireNonNull(env.getProperty("jwt.token.secret"))))
                .acceptExpiresAt(
                        Long.parseLong(Objects.requireNonNull(env.getProperty("jwt.token.expiration"))))
                .withIssuer(email)
                .withSubject(tokenType.name())
                .build();
        return jwtVerifier.toString();
    }

    @Override
    public String createUpdatePasswordToken(UUID userId, TokenType tokenType) {
        JWTVerifier jwtVerifier = JWT.require(
                        Algorithm.HMAC256(Objects.requireNonNull(env.getProperty("jwt.token.secret"))))
                .acceptExpiresAt(
                        Long.parseLong(Objects.requireNonNull(env.getProperty("jwt.token.expiration"))))
                .withIssuer(userId.toString())
                .withSubject(tokenType.name())
                .build();
        return jwtVerifier.toString();
    }

    @Override
    public String extractEmailFromResetToken(Token token) {
        try {
            TokenType tokenType = TokenType.valueOf(JWT.require(
                    Algorithm.HMAC256(Objects.requireNonNull(env.getProperty("jwt.token.secret"
                    )))).build().verify(token.getValue()).getSubject());
            if (tokenType != TokenType.REQUEST_RESET_PASSWORD) {
                throw new TokenRequestTypeException("TokenRequestType is not REQUEST_RESET_PASSWORD");
            }
            return JWT.require(Algorithm.HMAC256(Objects.requireNonNull(env.getProperty("jwt.token.secret"))))
                    .build().verify(token.getValue()).getIssuer();
        } catch (TokenExpiredException e) {
            throw new CustomTokenExpiredException("Token expired", e.getExpiredOn());
        } catch (JWTVerificationException e) {
            throw new CustomJWTVerificationException("Invalid token", e);
        }
    }

    @Override
    public UUID extractUserIdFromUpdateToken(Token token) {
        try {
            TokenType tokenType = TokenType.valueOf(JWT.require(
                    Algorithm.HMAC256(Objects.requireNonNull(env.getProperty("jwt.token.secret"
                    )))).build().verify(token.getValue()).getSubject());
            if (tokenType != TokenType.REQUEST_UPDATE_PASSWORD) {
                throw new TokenRequestTypeException("TokenRequestType is not REQUEST_UPDATE_PASSWORD");
            }
            return UUID.fromString(JWT.require(Algorithm.HMAC256(Objects.requireNonNull(env.getProperty("jwt.token.secret"))))
                    .build().verify(token.getValue()).getIssuer());
        } catch (TokenExpiredException e) {
            throw new CustomTokenExpiredException("Token expired", e.getExpiredOn());
        } catch (JWTVerificationException e) {
            throw new CustomJWTVerificationException("Invalid token", e);
        }

    }

}
