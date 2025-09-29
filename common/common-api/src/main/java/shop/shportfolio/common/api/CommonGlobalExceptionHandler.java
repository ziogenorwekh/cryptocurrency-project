package shop.shportfolio.common.api;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;

import org.springframework.http.*;
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

//    @ExceptionHandler(TypeMismatchException.class)
//    public ResponseEntity<ExceptionResponse> handleTypeMismatchException(TypeMismatchException ex) {
//        log.warn("Type mismatch: {}", ex.getMessage(), ex);
//        String message = "The request parameter type is invalid. Please check.";
//        return ResponseEntity.badRequest().body(
//                new ExceptionResponse(message, 400, "Bad Request")
//        );
//    }


    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
                                                        HttpStatusCode status, WebRequest request) {
        log.warn("Type mismatch: {}", ex.getMessage(), ex);
        String message = "The request parameter type is invalid. Please check.";
        return ResponseEntity.badRequest().body(
                new ExceptionResponse(message, 400, "Bad Request")
        );
    }
//    ConstraintViolationException

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ExceptionResponse> handleUnsupportedOperationException(
            UnsupportedOperationException ex) {
        log.warn("Unsupported operation: {}", ex.getMessage(), ex);
        String message = "This operation is not supported.";
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
                new ExceptionResponse(message, 405, "Bad Request")
        );
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex,
                                                               HttpHeaders headers,
                                                               HttpStatusCode status, WebRequest request) {
        log.warn("Missing PathVariable: {}", ex.getMessage(), ex);
        String message = String.format("Required path variable '%s' is missing.", ex.getVariableName());
        return ResponseEntity.badRequest().body(new ExceptionResponse(message, 400, "Bad Request"));
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                         HttpHeaders headers, HttpStatusCode status,
                                                                         WebRequest request) {
        log.warn("HTTP Method Not Supported: {}", ex.getMessage(), ex);
        String message = String.format("HTTP method not supported. Supported methods: %s",
                Objects.requireNonNull(ex.getSupportedHttpMethods()).stream().map(HttpMethod::name)
                        .collect(Collectors.joining(", ")));
        return ResponseEntity.status(405).body(new ExceptionResponse(message, 405, "Method Not Allowed"));
    }

    @Override
    protected ResponseEntity<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex,
                                                                          HttpHeaders headers, HttpStatusCode status,
                                                                          WebRequest request) {
        log.warn("Max Upload Size Exceeded: {}", ex.getMessage(), ex);
        String message = "The uploaded file exceeds the maximum allowed size. Please reduce the file size and try again.";
        return ResponseEntity.status(413).body(new ExceptionResponse(message, 413, "Payload Too Large"));
    }

    @ExceptionHandler(UserNotAccessException.class)
    public ResponseEntity<ExceptionResponse> handleUserNotAccessException(UserNotAccessException e) {
        log.warn("User not access exception: {}", e.getMessage(), e);
        return ResponseEntity.status(403)
                .body(new ExceptionResponse(e.getMessage(), 403, "Forbidden"));
    }


    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers, HttpStatusCode status,
                                                                          WebRequest request) {
        log.warn("Missing request parameter: {}", ex.getMessage(), ex);
        String message = String.format("Required request parameter '%s' is missing.", ex.getParameterName());
        return ResponseEntity.badRequest().body(new ExceptionResponse(message, 400, "Bad Request"));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleUnhandledException(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(500).body(
                new ExceptionResponse("An internal server error occurred.",
                        500, "Internal Server Error")
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<ExceptionResponse>> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("Constraint violation: {}", ex.getMessage(), ex);
        List<ExceptionResponse> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> new ExceptionResponse(
                        violation.getPropertyPath().toString(),
                        violation.getMessage(),
                        400,
                        "Bad Request"))
                .toList();
        return ResponseEntity.badRequest().body(errors);
    }
}
