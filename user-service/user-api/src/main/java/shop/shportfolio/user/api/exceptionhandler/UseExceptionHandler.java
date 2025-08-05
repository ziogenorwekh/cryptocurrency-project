package shop.shportfolio.user.api.exceptionhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shop.shportfolio.common.api.CommonGlobalExceptionHandler;
import shop.shportfolio.common.message.ExceptionResponse;
import shop.shportfolio.common.exception.UserNotAccessException;
import shop.shportfolio.user.application.exception.*;
import shop.shportfolio.user.application.exception.mail.CustomMailSendException;
import shop.shportfolio.user.application.exception.s3.CustomAmazonS3Exception;
import shop.shportfolio.user.application.exception.security.CustomJWTVerificationException;
import shop.shportfolio.user.application.exception.security.CustomTokenExpiredException;
import shop.shportfolio.user.application.exception.security.TokenRequestTypeException;
import shop.shportfolio.user.domain.exception.UserDomainException;

@Slf4j
@RestControllerAdvice
public class UseExceptionHandler extends CommonGlobalExceptionHandler {


    @ExceptionHandler(CustomMailSendException.class)
    public ResponseEntity<ExceptionResponse> handleCustomMailSendException(CustomMailSendException e) {
        log.error("Mail send error: {}", e.getMessage(), e);
        return ResponseEntity.status(500)
                .body(new ExceptionResponse("메일 전송 중 오류가 발생했습니다.", 500, "Internal Server Error"));
    }

    @ExceptionHandler(CustomAmazonS3Exception.class)
    public ResponseEntity<ExceptionResponse> handleCustomAmazonS3Exception(CustomAmazonS3Exception e) {
        log.error("Amazon S3 error: {}", e.getMessage(), e);
        return ResponseEntity.status(500)
                .body(new ExceptionResponse("파일 업로드 중 오류가 발생했습니다.", 500, "Internal Server Error"));
    }

    @ExceptionHandler(InvalidAuthCodeException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidAuthCodeException(InvalidAuthCodeException e) {
        log.warn("Invalid auth code: {}", e.getMessage(), e);
        return ResponseEntity.status(401)
                .body(new ExceptionResponse("인증 코드가 유효하지 않거나 만료되었습니다.", 401, "Unauthorized"));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidObjectException(InvalidPasswordException e) {
        log.warn("Invalid password: {}", e.getMessage(), e);
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse(e.getMessage(), 400, "Bad Request"));
    }

    @ExceptionHandler(NotImplementedException.class)
    public ResponseEntity<ExceptionResponse> handleNotImplementedException(NotImplementedException e) {
        log.warn("Not implemented feature accessed: {}", e.getMessage(), e);
        return ResponseEntity.status(501)
                .body(new ExceptionResponse("해당 기능은 아직 구현되지 않았습니다.", 501, "Not Implemented"));
    }

    @ExceptionHandler(TokenRequestTypeException.class)
    public ResponseEntity<ExceptionResponse> handleTokenRequestTypeException(TokenRequestTypeException e) {
        log.warn("Invalid token request type: {}", e.getMessage(), e);
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse(e.getMessage(), 400, "Bad Request"));
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidRequestException(InvalidRequestException e) {
        log.warn("Invalid request: {}", e.getMessage(), e);
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse(e.getMessage(), 400, "Bad Request"));
    }

    @ExceptionHandler(TransactionHistoryNotfoundException.class)
    public ResponseEntity<ExceptionResponse> handleTransactionHistoryNotfoundException(TransactionHistoryNotfoundException e) {
        log.warn("Transaction history not found: {}", e.getMessage(), e);
        return ResponseEntity.status(404)
                .body(new ExceptionResponse("거래 기록을 찾을 수 없습니다.", 404, "Not Found"));
    }

    @ExceptionHandler(UserApplicationException.class)
    public ResponseEntity<ExceptionResponse> handleUserApplicationException(UserApplicationException e) {
        log.warn("User application exception: {}", e.getMessage(), e);
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse(e.getMessage(), 400, "Bad Request"));
    }

    @ExceptionHandler(UserDuplicationException.class)
    public ResponseEntity<ExceptionResponse> handleUserDuplicationException(UserDuplicationException e) {
        log.warn("User duplication error: {}", e.getMessage(), e);
        return ResponseEntity.status(409)
                .body(new ExceptionResponse(e.getMessage(), 409, "Conflict"));
    }

    @ExceptionHandler(UserNotfoundException.class)
    public ResponseEntity<ExceptionResponse> handleUserNotfoundException(UserNotfoundException e) {
        log.warn("User not found: {}", e.getMessage(), e);
        return ResponseEntity.status(404)
                .body(new ExceptionResponse(e.getMessage(), 404, "Not Found"));
    }

    @ExceptionHandler(UserDomainException.class)
    public ResponseEntity<ExceptionResponse> handleUserDomainException(UserDomainException e) {
        log.warn("User domain exception: {}", e.getMessage(), e);
        return ResponseEntity.status(500)
                .body(new ExceptionResponse("사용자 도메인에서 오류가 발생했습니다.", 500, "Internal Server Error"));
    }

    @ExceptionHandler(CustomTokenExpiredException.class)
    public ResponseEntity<ExceptionResponse> handleCustomTokenExpiredException(CustomTokenExpiredException e) {
        log.warn("Token expired: {}", e.getMessage(), e);
        return ResponseEntity.status(401)
                .body(new ExceptionResponse("토큰이 만료되었습니다.", 401, "Unauthorized"));
    }

    @ExceptionHandler(CustomJWTVerificationException.class)
    public ResponseEntity<ExceptionResponse> handleCustomJWTVerificationException(CustomJWTVerificationException e) {
        log.warn("JWT verification failed: {}", e.getMessage(), e);
        return ResponseEntity.status(401)
                .body(new ExceptionResponse("유효하지 않은 토큰입니다.", 401, "Unauthorized"));
    }
}
