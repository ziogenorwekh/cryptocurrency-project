package shop.shportfolio.common.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import shop.shportfolio.common.exception.UserNotAccessException;
import shop.shportfolio.common.message.ExceptionResponse;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class CommonGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalStateException(IllegalStateException ex) {
        log.warn("Illegal state: {}", ex.getMessage(), ex);
        return ResponseEntity.status(409).body(new ExceptionResponse(ex.getMessage(), 409, "Conflict"));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(new ExceptionResponse(ex.getMessage(), 400, "Bad Request"));
    }



    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ExceptionResponse> handleTypeMismatchException(TypeMismatchException ex) {
        log.warn("Type mismatch: {}", ex.getMessage(), ex);
        String message = "요청 파라미터의 타입이 올바르지 않습니다. 확인해 주세요.";
        return ResponseEntity.badRequest().body(
                new ExceptionResponse(message, 400, "Bad Request")
        );
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex,
                                                               HttpHeaders headers,
                                                               HttpStatusCode status, WebRequest request) {
        log.warn("Missing PathVariable: {}", ex.getMessage(), ex);
        String message = String.format("필수 경로 변수 '%s'가 누락되었습니다.", ex.getVariableName());
        return ResponseEntity.badRequest().body(new ExceptionResponse(message, 400, "Bad Request"));
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                         HttpHeaders headers, HttpStatusCode status,
                                                                         WebRequest request) {
        log.warn("HTTP Method Not Supported: {}", ex.getMessage(), ex);
        String message = String.format("지원하지 않는 HTTP 메서드입니다. 사용 가능한 메서드: %s",
                Objects.requireNonNull(ex.getSupportedHttpMethods()).stream().map(HttpMethod::name)
                        .collect(Collectors.joining(", ")));
        return ResponseEntity.status(405).body(new ExceptionResponse(message, 405, "Method Not Allowed"));
    }

    @Override
    protected ResponseEntity<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex,
                                                                          HttpHeaders headers, HttpStatusCode status,
                                                                          WebRequest request) {
        log.warn("Max Upload Size Exceeded: {}", ex.getMessage(), ex);
        String message = "업로드 가능한 파일 크기를 초과했습니다. 파일 크기를 줄여 다시 시도해 주세요.";
        return ResponseEntity.status(413).body(new ExceptionResponse(message, 413, "Payload Too Large"));
    }

    @ExceptionHandler(UserNotAccessException.class)
    public ResponseEntity<ExceptionResponse> handleUserNotAccessException(UserNotAccessException e) {
        log.warn("User not access exception: {}", e.getMessage(), e);
        return ResponseEntity.status(403)
                .body(new ExceptionResponse(e.getMessage(), 403, "Forbidden"));
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
                new ExceptionResponse("서버 내부 오류가 발생했습니다.",
                        500, "Internal Server Error")
        );
    }
}
