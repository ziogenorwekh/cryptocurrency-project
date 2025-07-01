package shop.shportfolio.user.security.adapter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import shop.shportfolio.user.application.ports.output.security.AuthenticatorPort;
import shop.shportfolio.common.domain.valueobject.TokenType;
import shop.shportfolio.user.application.exception.security.TokenRequestTypeException;
import shop.shportfolio.user.domain.entity.Role;
import shop.shportfolio.user.security.model.CustomUserDetails;

import java.util.Arrays;
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
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManager.authenticate(token);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUserId();
    }

    @Override
    public String generateLoginToken(UUID userId, List<Role> roles) {
        String[] arr = new String[roles.size()];
        for (int i = 0; i < roles.size(); i++) {
            String roleType = roles.get(i).getRoleType().name();
            arr[i] = roleType;
        }
        JWTVerifier jwtVerifier = JWT.require(
                        Algorithm.HMAC256(Objects.requireNonNull(env.getProperty("jwt.token.secret"))))
                .acceptExpiresAt(
                        Long.parseLong(Objects.requireNonNull(env.getProperty("jwt.token.expiration"))))
                .withIssuer(userId.toString())
                .withSubject(TokenType.COMPLETED.name())
                .withArrayClaim("Roles",arr)
                .build();
        return jwtVerifier.toString();
    }

    @Override
    public String generate2FATmpToken(String email) {
        JWTVerifier jwtVerifier = JWT.require(
                        Algorithm.HMAC256(Objects.requireNonNull(env.getProperty("jwt.token.secret"))))
                .acceptExpiresAt(
                        Long.parseLong(Objects.requireNonNull(env.getProperty("jwt.token.expiration"))))
                .withIssuer(email)
                .withSubject(TokenType.REQUIRE_2FA.name())
                .build();
        return jwtVerifier.toString();
    }

    @Override
    public String getEmailBy2FATmpToken(String token) {
        try {
            TokenType tokenType = TokenType.valueOf(JWT.require(
                    Algorithm.HMAC256(Objects.requireNonNull(env.getProperty("jwt.token.secret"
                    )))).build().verify(token).getSubject());
            if (tokenType != TokenType.REQUIRE_2FA) {
                throw new TokenRequestTypeException("TokenRequestType is not REQUIRE_2FA");
            }
            return JWT.require(Algorithm.HMAC256(Objects.requireNonNull(env.getProperty("jwt.token.secret"))))
                    .build().verify(token).getIssuer();
        } catch (TokenExpiredException e) {
            throw new TokenExpiredException("Token expired", e.getExpiredOn());
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException("Invalid token", e);
        }
    }
}
