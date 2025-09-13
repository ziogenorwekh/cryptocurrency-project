package shop.shportfolio.matching.api.exceptionhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shop.shportfolio.common.api.CommonGlobalExceptionHandler;
import shop.shportfolio.common.message.ExceptionResponse;
import shop.shportfolio.matching.application.exception.OrderBookNotFoundException;

@Slf4j
@RestControllerAdvice
public class MatchingExceptionHandler extends CommonGlobalExceptionHandler {

    @ExceptionHandler(OrderBookNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleOrderBookNotFoundException(OrderBookNotFoundException ex) {
        log.warn("OrderBookNotFoundException is -> {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ExceptionResponse(ex.getMessage(),404,"Not Found")
        );
    }
}
