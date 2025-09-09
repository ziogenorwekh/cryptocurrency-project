package shop.shportfolio.user.infrastructure.security.adapter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import shop.shportfolio.user.application.exception.security.CustomJWTVerificationException;
import shop.shportfolio.user.application.exception.security.CustomTokenExpiredException;
import shop.shportfolio.user.application.ports.output.security.JwtTokenPort;
import shop.shportfolio.user.domain.valueobject.Token;
import shop.shportfolio.common.domain.valueobject.TokenType;
import shop.shportfolio.user.application.exception.security.TokenRequestTypeException;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Component
public class JwtTokenAdapter implements JwtTokenPort {

    private final Environment env;

    public JwtTokenAdapter(Environment env) {
        this.env = env;
    }

    private Algorithm getAlgorithm() {
        String secret = Objects.requireNonNull(env.getProperty("jwt.token.secret"));
        return Algorithm.HMAC256(secret);
    }

    private long getExpirationMs() {
        return Long.parseLong(Objects.requireNonNull(env.getProperty("jwt.token.expiration")));
    }

    @Override
    public String extractEmailFromResetToken(Token token) {
        try {
            if (token == null || token.getValue() == null) {
                throw new IllegalArgumentException("Token cannot be null");
            }

            DecodedJWT decoded = JWT.require(getAlgorithm())
                    .build()
                    .verify(token.getValue());

            TokenType tokenType = TokenType.valueOf(decoded.getSubject());
            if (tokenType != TokenType.REQUEST_RESET_PASSWORD) {
                throw new TokenRequestTypeException("TokenRequestType is not REQUEST_RESET_PASSWORD");
            }
            return decoded.getIssuer();
        } catch (TokenExpiredException e) {
            throw new CustomTokenExpiredException("Token expired", e.getExpiredOn());
        } catch (JWTVerificationException | NullPointerException | IllegalArgumentException e) {
            throw new CustomJWTVerificationException("Invalid token or JWT config", e);
        } catch (Exception e) {
            throw new CustomJWTVerificationException("Unexpected error while parsing token", e);
        }
    }

    @Override
    public UUID extractUserIdFromUpdateToken(Token token) {
        try {
            if (token == null || token.getValue() == null) {
                throw new IllegalArgumentException("Token cannot be null");
            }

            DecodedJWT decoded = JWT.require(getAlgorithm())
                    .build()
                    .verify(token.getValue());

            TokenType tokenType = TokenType.valueOf(decoded.getSubject());
            if (tokenType != TokenType.REQUEST_UPDATE_PASSWORD) {
                throw new TokenRequestTypeException("TokenRequestType is not REQUEST_UPDATE_PASSWORD");
            }
            return UUID.fromString(decoded.getIssuer());
        } catch (TokenExpiredException e) {
            throw new CustomTokenExpiredException("Token expired", e.getExpiredOn());
        } catch (JWTVerificationException | NullPointerException | IllegalArgumentException e) {
            throw new CustomJWTVerificationException("Invalid token or JWT config", e);
        } catch (Exception e) {
            throw new CustomJWTVerificationException("Unexpected error while parsing token", e);
        }
    }

    @Override
    public String generateResetTokenByEmail(String email, TokenType tokenType) {
        try {
            if (email == null || tokenType == null) throw new IllegalArgumentException("Email or TokenType cannot be null");
            Date expiresAt = new Date(System.currentTimeMillis() + getExpirationMs());
            return JWT.create()
                    .withIssuer(email)
                    .withSubject(tokenType.name())
                    .withExpiresAt(expiresAt)
                    .sign(getAlgorithm());
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new CustomJWTVerificationException("JWT config invalid or invalid input", e);
        } catch (Exception e) {
            throw new CustomJWTVerificationException("Failed to generate reset token", e);
        }
    }

    @Override
    public String createUpdatePasswordToken(UUID userId, TokenType tokenType) {
        try {
            if (userId == null || tokenType == null) throw new IllegalArgumentException("UserId or TokenType cannot be null");
            Date expiresAt = new Date(System.currentTimeMillis() + getExpirationMs());
            return JWT.create()
                    .withIssuer(userId.toString())
                    .withSubject(tokenType.name())
                    .withExpiresAt(expiresAt)
                    .sign(getAlgorithm());
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new CustomJWTVerificationException("JWT config invalid or invalid input", e);
        } catch (Exception e) {
            throw new CustomJWTVerificationException("Failed to generate update password token", e);
        }
    }
}
