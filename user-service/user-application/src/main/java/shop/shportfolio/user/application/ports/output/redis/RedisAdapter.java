package shop.shportfolio.user.application.ports.output.redis;

import shop.shportfolio.common.domain.valueobject.AuthCodeType;

import java.util.Optional;

public interface RedisAdapter {
    /**
     * 인증 코드를 Redis에 저장
     * @param type 인증 코드의 용도 (예: 이메일 인증, 2FA 이메일, 2FA SMS 등)
     * @param identifier 인증 대상 식별자 (이메일 주소, 전화번호 등)
     * @param code 저장할 인증 코드
     */
    void saveAuthCode(AuthCodeType type, String identifier, String code);
    /**
     * Redis에서 인증 코드를 조회
     * @param type 인증 코드의 용도
     * @param identifier 인증 대상 식별자
     * @return 저장된 인증 코드가 존재하면 Optional로 반환, 없으면 Optional.empty()
     */
    Optional<String> getAuthCode(AuthCodeType type, String identifier);
    /**
     * Redis에서 인증 코드를 삭제
     * @param type 인증 코드의 용도
     * @param identifier 인증 대상 식별자
     */
    void deleteAuthCode(AuthCodeType type, String identifier);
    /**
     * 입력한 인증 코드가 Redis에 저장된 코드와 일치하는지 검증
     * @param type 인증 코드의 용도
     * @param identifier 인증 대상 식별자
     * @param code 사용자가 제출한 인증 코드
     * @return 코드가 일치하면 true, 아니면 false
     */
    boolean verifyAuthCode(AuthCodeType type, String identifier, String code);
}