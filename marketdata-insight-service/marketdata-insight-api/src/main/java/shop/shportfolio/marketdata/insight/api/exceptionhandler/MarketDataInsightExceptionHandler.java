package shop.shportfolio.marketdata.insight.api.exceptionhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shop.shportfolio.common.api.CommonGlobalExceptionHandler;
import shop.shportfolio.common.message.ExceptionResponse;
import shop.shportfolio.marketdata.insight.application.exception.*;

@Slf4j
@RestControllerAdvice
public class MarketDataInsightExceptionHandler extends CommonGlobalExceptionHandler {


    @ExceptionHandler(BithumbAPIRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBithumbAPIRequestException(BithumbAPIRequestException ex) {
        log.warn("Bithumb API request error: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionResponse(ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),"Bad Request"));
    }

    @ExceptionHandler(MarketDataInsightApplicationException.class)
    public ResponseEntity<ExceptionResponse> handleMarketDataInsightApplicationException(MarketDataInsightApplicationException ex) {
        log.warn("MarketDataInsight application error: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionResponse(ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),"Bad Request"));
    }

    @ExceptionHandler(MarketItemNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleMarketItemNotFoundException(MarketItemNotFoundException ex) {
        log.warn("Market item not found: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionResponse(ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),"Not Found"));
    }
    @ExceptionHandler(NotSupportAnalysisTypeException.class)
    public ResponseEntity<ExceptionResponse> handleNotSupportAnalysisTypeException(NotSupportAnalysisTypeException ex) {
        log.warn("Not supported analysis type: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionResponse(ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),"Bad Request"));
    }
    @ExceptionHandler(OpenAPIRequestException.class)
    public ResponseEntity<ExceptionResponse> handleOpenAPIRequestException(OpenAPIRequestException ex) {
        log.warn("Open API request error: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ExceptionResponse(ex.getMessage(),
                HttpStatus.SERVICE_UNAVAILABLE.value(),"Service Unavailable"));
    }

}
