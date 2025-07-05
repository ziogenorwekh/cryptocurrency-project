package shop.shportfolio.user.application.ports.output.security;

import shop.shportfolio.user.domain.valueobject.Token;
import shop.shportfolio.common.domain.valueobject.TokenType;

import java.util.UUID;

public interface JwtTokenPort {

    String generateResetTokenByEmail(String email, TokenType tokenType);

    String createUpdatePasswordToken(UUID userId, TokenType tokenType);

    String extractEmailFromResetToken(Token token);

    UUID extractUserIdFromUpdateToken(Token token);
}
