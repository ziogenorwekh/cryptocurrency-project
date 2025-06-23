package shop.shportfolio.common.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import shop.shportfolio.common.message.ExceptionResponse;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CommonGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ExceptionResponse> handleTypeMismatchException(TypeMismatchException ex) {
        log.warn("Type mismatch: {}", ex.getMessage(), ex);
        String message = "요청 파라미터의 타입이 올바르지 않습니다. 확인해 주세요.";
        return ResponseEntity.badRequest().body(
                new ExceptionResponse(message, 400, "Bad Request")
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ExceptionResponse> handleMissingRequestParam(MissingServletRequestParameterException ex) {
        log.warn("Missing request parameter: {}", ex.getMessage(), ex);
        String message = String.format("필수 요청 파라미터 '%s'가 누락되었습니다.", ex.getParameterName());
        return ResponseEntity.badRequest().body(new ExceptionResponse(message, 400, "Bad Request"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ExceptionResponse>> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.warn("Validation failed: {}", ex.getMessage(), ex);

        List<ExceptionResponse> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ExceptionResponse(
                        error.getField(),
                        error.getDefaultMessage(),
                        400,
                        "Bad Request"
                ))
                .collect(Collectors.toList());

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(new ExceptionResponse(ex.getMessage(), 400, "Bad Request"));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalState(IllegalStateException ex) {
        log.warn("Illegal state: {}", ex.getMessage(), ex);
        return ResponseEntity.status(409).body(new ExceptionResponse(ex.getMessage(), 409, "Conflict"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleUnhandledException(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(500).body(
                new ExceptionResponse("서버 내부 오류가 발생했습니다.", 500, "Internal Server Error")
        );
    }
}
