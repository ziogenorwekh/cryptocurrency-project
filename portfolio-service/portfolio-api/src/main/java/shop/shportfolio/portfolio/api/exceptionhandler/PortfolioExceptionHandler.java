package shop.shportfolio.portfolio.api.exceptionhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shop.shportfolio.common.api.CommonGlobalExceptionHandler;
import shop.shportfolio.common.message.ExceptionResponse;
import shop.shportfolio.portfolio.application.exception.*;
import shop.shportfolio.portfolio.domain.exception.PortfolioDomainException;

@Slf4j
@RestControllerAdvice
public class PortfolioExceptionHandler extends CommonGlobalExceptionHandler {


    @ExceptionHandler(BalanceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleBalanceNotFound(BalanceNotFoundException ex) {
        log.warn("Illegal state: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionResponse(ex.getMessage(),
                HttpStatus.NOT_FOUND.value(), "Not Found"));
    }

    @ExceptionHandler(DepositFailedException.class)
    public ResponseEntity<ExceptionResponse> handleDepositFailed(DepositFailedException ex) {
        log.warn("Illegal state: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionResponse(ex.getMessage(),
                HttpStatus.CONFLICT.value(), "Conflict"));
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidRequest(InvalidRequestException ex) {
        log.warn("Illegal state: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionResponse(ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(), "Bad Request"));
    }
    @ExceptionHandler(PortfolioExistException.class)
    public ResponseEntity<ExceptionResponse> handlePortfolioExist(PortfolioExistException ex) {
        log.warn("Illegal state: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionResponse(ex.getMessage(),
                HttpStatus.NOT_FOUND.value(), "Not Found"));
    }
    @ExceptionHandler(PortfolioNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handlePortfolioNotFound(PortfolioNotFoundException ex) {
        log.warn("Illegal state: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionResponse(ex.getMessage(),
                HttpStatus.NOT_FOUND.value(), "Not Found"));
    }

    @ExceptionHandler(PortfolioDomainException.class)
    public ResponseEntity<ExceptionResponse> handlePortfolioDomain(PortfolioDomainException ex) {
        log.warn("Illegal state: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionResponse(ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(), "Bad Request"));
    }

    @ExceptionHandler(PortfolioApplicationException.class)
    public ResponseEntity<ExceptionResponse> handlePortfolioApplication(PortfolioApplicationException ex) {
        log.warn("Illegal state: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionResponse(ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(), "Bad Request"));
    }


}
