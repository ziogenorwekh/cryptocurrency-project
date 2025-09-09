package shop.shportfolio.user.infrastructure.security.adapter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import shop.shportfolio.user.application.exception.security.CustomBadCredentialsException;
import shop.shportfolio.user.application.exception.security.CustomJwtException;
import shop.shportfolio.user.application.exception.security.CustomTokenExpiredException;
import shop.shportfolio.user.application.ports.output.security.AuthenticatorPort;
import shop.shportfolio.common.domain.valueobject.TokenType;
import shop.shportfolio.user.application.exception.security.TokenRequestTypeException;
import shop.shportfolio.user.domain.entity.Role;
import shop.shportfolio.user.infrastructure.security.model.CustomUserDetails;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Component
public class CompositeAuthenticator implements AuthenticatorPort {

    private final AuthenticationManager authenticationManager;
    private final Environment env;

    @Autowired
    public CompositeAuthenticator(AuthenticationManager authenticationManager, Environment env) {
        this.authenticationManager = authenticationManager;
        this.env = env;
    }

    @Override
    public UUID authenticate(String email, String password) {
        try {
            if (email == null || password == null) {
                throw new BadCredentialsException("Email or password is null");
            }
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
            Authentication authentication = authenticationManager.authenticate(token);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUserId();
        } catch (BadCredentialsException e) {
            throw new CustomBadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new CustomBadCredentialsException("Authentication failed");
        }
    }

    @Override
    public String generateLoginToken(UUID userId, List<Role> roles) {
        try {
            if (roles == null || roles.isEmpty()) {
                throw new IllegalArgumentException("Roles cannot be null or empty");
            }
            String[] arr = roles.stream().map(r -> r.getRoleType().name()).toArray(String[]::new);
            long expiration = Long.parseLong(Objects.requireNonNull(env.getProperty("jwt.token.expiration")));
            String secret = Objects.requireNonNull(env.getProperty("jwt.token.secret"));
            return JWT.create()
                    .withIssuer(userId.toString())
                    .withSubject(TokenType.COMPLETED.name())
                    .withArrayClaim("Roles", arr)
                    .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                    .sign(Algorithm.HMAC256(secret));
        } catch (NullPointerException | NumberFormatException e) {
            throw new CustomJwtException("JWT config is invalid", e);
        } catch (Exception e) {
            throw new CustomJwtException("Failed to generate login token", e);
        }
    }

    @Override
    public String generate2FATmpToken(String email) {
        try {
            if (email == null) throw new IllegalArgumentException("Email cannot be null");
            long expiration = Long.parseLong(Objects.requireNonNull(env.getProperty("jwt.token.expiration")));
            String secret = Objects.requireNonNull(env.getProperty("jwt.token.secret"));
            // JWTVerifier 자체를 반환할 수 없으므로, 실제 token 생성은 JWT.create() 해야 함
            return JWT.create()
                    .withIssuer(email)
                    .withSubject(TokenType.REQUIRE_2FA.name())
                    .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                    .sign(Algorithm.HMAC256(secret));
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new CustomJwtException("JWT config is invalid", e);
        } catch (Exception e) {
            throw new CustomJwtException("Failed to generate 2FA token", e);
        }
    }

    @Override
    public String getEmailBy2FATmpToken(String token) {
        try {
            if (token == null) throw new IllegalArgumentException("Token cannot be null");
            String secret = Objects.requireNonNull(env.getProperty("jwt.token.secret"));
            TokenType tokenType = TokenType.valueOf(JWT.require(Algorithm.HMAC256(secret)).build().verify(token).getSubject());
            if (tokenType != TokenType.REQUIRE_2FA) {
                throw new TokenRequestTypeException("TokenRequestType is not REQUIRE_2FA");
            }
            return JWT.require(Algorithm.HMAC256(secret)).build().verify(token).getIssuer();
        } catch (TokenExpiredException e) {
            throw new CustomTokenExpiredException("Token expired", e.getExpiredOn());
        } catch (JWTVerificationException | IllegalArgumentException | NullPointerException e) {
            throw new CustomJwtException("Invalid token or config", e);
        } catch (Exception e) {
            throw new CustomJwtException("Failed to parse 2FA token", e);
        }
    }
}
