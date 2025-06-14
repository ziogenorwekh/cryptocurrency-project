package shop.shportfolio.user.application.ports.output.redis;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public interface RedisAdapter {
    // ✅ [임시 이메일 인증 코드 저장 및 검증]
    // TimeUnit 으로 특정 시간에 종속되지 않고 자유롭게 설정
    // redisAdapter.savePasswordResetToken(userId, token, 15, TimeUnit.MINUTES);
    String saveTempEmailCode(String email,String code, long timeout, TimeUnit timeUnit);
    boolean verifyTempEmailAuthCode(String email, String code);
    // redis 에서 임시 이메일 코드를 삭제
    void deleteTempEmailAuthentication(String email);

    // 레디스에 임시 유저가 저장되었는지 판단
    boolean isAuthenticatedTempUserId(UUID userId);

    // ✅ [임시 유저 ID 저장 (ex. 비밀번호 재설정 등)]
    String saveTempUserId(UUID userId, String email, long timeout, TimeUnit timeUnit);

    
}
