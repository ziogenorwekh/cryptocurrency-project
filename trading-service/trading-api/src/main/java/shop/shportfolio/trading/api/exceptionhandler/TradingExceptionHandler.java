package shop.shportfolio.trading.api.exceptionhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shop.shportfolio.common.api.CommonGlobalExceptionHandler;
import shop.shportfolio.common.exception.UserNotAccessException;
import shop.shportfolio.common.message.ExceptionResponse;
import shop.shportfolio.trading.application.exception.*;
import shop.shportfolio.trading.domain.exception.TradingDomainException;

@Slf4j
@RestControllerAdvice
public class TradingExceptionHandler extends CommonGlobalExceptionHandler {

    @ExceptionHandler(BithumbAPIRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBithumbAPIRequestException(BithumbAPIRequestException e) {
        log.warn("BithumbApi request exception: {}", e.getMessage(), e);
        return ResponseEntity.status(500)
                .body(new ExceptionResponse(e.getMessage(), 403, "Forbidden"));
    }

    @ExceptionHandler(AlreadyOrderPendingException.class)
    public ResponseEntity<ExceptionResponse> handleAlreadyOrderPendingException(AlreadyOrderPendingException e) {
        log.warn("already order pending exception : {}", e.getMessage(), e);
        return ResponseEntity.status(400)
                .body(new ExceptionResponse(e.getMessage(), 400, "Bad Request"));
    }
    @ExceptionHandler(TradingDomainException.class)
    public ResponseEntity<ExceptionResponse> handleTradingDomainException(TradingDomainException e) {
        log.warn("domain exception : {}", e.getMessage(), e);
        return ResponseEntity.status(400)
                .body(new ExceptionResponse(e.getMessage(), 400, "Bad Request"));
    }

    @ExceptionHandler(CouponNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleCouponNotFoundException(CouponNotFoundException e) {
        log.warn("Coupon not found: {}", e.getMessage(), e);
        return ResponseEntity.status(404)
                .body(new ExceptionResponse(e.getMessage(), 400, "Not found"));
    }

    @ExceptionHandler(MarketItemNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleMarketItemNotFoundException(MarketItemNotFoundException e) {
        log.warn("Market item not found: {}", e.getMessage(), e);
        return ResponseEntity.status(400)
                .body(new ExceptionResponse(e.getMessage(), 400, "Not found"));
    }
    @ExceptionHandler(MarketPausedException.class)
    public ResponseEntity<ExceptionResponse> handleMarketPausedException(MarketPausedException e) {
        log.warn("Market paused: {}", e.getMessage(), e);
        return ResponseEntity.status(500)
                .body(new ExceptionResponse(e.getMessage(), 500, "Internal Server Error"));
    }
    @ExceptionHandler(OrderBookNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleOrderBookNotFoundException(OrderBookNotFoundException e) {
        log.warn("Order book not found: {}", e.getMessage(), e);
        return ResponseEntity.status(400)
                .body(new ExceptionResponse(e.getMessage(), 400, "Not found"));
    }
    @ExceptionHandler(OrderInValidatedException.class)
    public ResponseEntity<ExceptionResponse> handleOrderInValidatedException(OrderInValidatedException e) {
        log.warn("Order invalidated: {}", e.getMessage(), e);
        return ResponseEntity.status(400)
                .body(new ExceptionResponse(e.getMessage(), 400, "Bad Request"));
    }
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleOrderNotFoundException(OrderNotFoundException e) {
        log.warn("Order not found: {}", e.getMessage(), e);
        return ResponseEntity.status(404)
                .body(new ExceptionResponse(e.getMessage(), 404, "Not found"));
    }
    @ExceptionHandler(TickPriceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleTickPriceNotFoundException(TickPriceNotFoundException e) {
        log.warn("TickPrice not found: {}", e.getMessage(), e);
        return ResponseEntity.status(404)
                .body(new ExceptionResponse(e.getMessage(), 404, "Not found"));
    }

    @ExceptionHandler(TradingApplicationException.class)
    public ResponseEntity<ExceptionResponse> handleTradingApplicationException(TradingApplicationException e) {
        log.warn("Trading application exception: {}", e.getMessage(), e);
        return ResponseEntity.status(400)
                .body(new ExceptionResponse(e.getMessage(), 400, "Bad Request"));
    }
    @ExceptionHandler(UserBalanceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleUserBalanceNotFoundException(UserBalanceNotFoundException e) {
        log.warn("User balance not found: {}", e.getMessage(), e);
        return ResponseEntity.status(404)
                .body(new ExceptionResponse(e.getMessage(), 404, "Not found"));
    }
}
