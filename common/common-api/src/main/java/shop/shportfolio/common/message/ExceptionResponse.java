package shop.shportfolio.common.message;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ExceptionResponse {

    private String field;
    private final String message;
    private final int status;
    private final String error;

    public ExceptionResponse(String field, String message, int status, String error) {
        this.field = field;
        this.message = message;
        this.status = status;
        this.error = error;
    }

    public ExceptionResponse(String message, int status, String error) {
        this(null, message, status, error);
    }
}
