package shop.shportfolio.user.infrastructure.redis.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import shop.shportfolio.user.application.ports.output.redis.RedisAdapter;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class RedisAdapterImpl implements RedisAdapter {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisAdapterImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String saveTempEmailCode(String email, String code, long timeout, TimeUnit timeUnit) {
        String key = buildTempEmailCodeKey(email);
        redisTemplate.opsForValue().set(key, code, timeout, timeUnit);
        return key;
    }

    @Override
    public boolean verifyTempEmailAuthCode(String email, String code) {
        String key = buildTempEmailCodeKey(email);
        Object storedCode = redisTemplate.opsForValue().get(key);
        return code != null && code.equals(storedCode);
    }

    @Override
    public void deleteTempEmailCode(String email) {
        String key = buildTempEmailCodeKey(email);
        redisTemplate.delete(key);
    }

    @Override
    public boolean isAuthenticatedTempUserId(UUID userId) {
        String key = buildTempUserIdKey(userId);
        return redisTemplate.hasKey(key);
    }

    @Override
    public String saveTempUserId(UUID userId, String email, long timeout, TimeUnit timeUnit) {
        String key = buildTempUserIdKey(userId);
        redisTemplate.opsForValue().set(key, email, timeout, timeUnit);
        return key;
    }

    @Override
    public String save2FAEmailCode(String email, String code, long timeout, TimeUnit timeUnit) {
        String key = build2FAEmailCodeKey(email);
        redisTemplate.opsForValue().set(key, code, timeout, timeUnit);
        return key;
    }

    @Override
    public void delete2FASettingEmailCode(String email) {
        String key = build2FAEmailCodeKey(email);
        redisTemplate.delete(key);
    }

    @Override
    public Boolean isSave2FAEmailCode(String email, String code) {
        String key = build2FAEmailCodeKey(email);
        Object storedCode = redisTemplate.opsForValue().get(key);
        return code != null && code.equals(storedCode);
    }

    @Override
    public void save2FALoginCode(String email, String code, long timeout, TimeUnit timeUnit) {
        String key = build2FALoginCodeKey(email);
        redisTemplate.opsForValue().set(key, code, timeout, timeUnit);
    }

    @Override
    public Boolean isSave2FALoginCode(String email, String code) {
        String key = build2FALoginCodeKey(email);
        Object storedCode = redisTemplate.opsForValue().get(key);
        return code != null && code.equals(storedCode);
    }

    @Override
    public void delete2FALoginCode(String email) {
        String key = build2FALoginCodeKey(email);
        redisTemplate.delete(key);
    }

    private String buildTempEmailCodeKey(String email) {
        return "temp:email:code:" + email;
    }

    private String buildTempUserIdKey(UUID userId) {
        return "temp:user:id:" + userId.toString();
    }

    private String build2FAEmailCodeKey(String email) {
        return "2fa:email:code:" + email;
    }

    private String build2FALoginCodeKey(String email) {
        return "2fa:login:code:" + email;
    }
}
