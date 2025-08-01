package shop.shportfolio.coupon.api.exceptionHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shop.shportfoilo.coupon.domain.exception.CouponDomainException;
import shop.shportfolio.common.api.CommonGlobalExceptionHandler;
import shop.shportfolio.common.message.ExceptionResponse;
import shop.shportfolio.coupon.application.exception.*;

@Slf4j
@RestControllerAdvice
public class CouponExceptionHandler extends CommonGlobalExceptionHandler {

    @ExceptionHandler(TossAPIException.class)
    public ResponseEntity<ExceptionResponse> handleTossAPIException(TossAPIException e) {
        log.error("toss api error is : {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Internal Server Error"));
    }

    @ExceptionHandler(CouponGradeException.class)
    public ResponseEntity<ExceptionResponse> handleCouponGradeException(CouponGradeException e) {
        log.error("coupon grade error is : {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(e.getMessage(), HttpStatus.NOT_FOUND.value(),
                        "Coupon Grade Not Found"));
    }
    @ExceptionHandler(CouponNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleCouponNotFoundException(CouponNotFoundException e) {
        log.error("coupon not found error is : {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(e.getMessage(), HttpStatus.NOT_FOUND.value(),
                        "Coupon Not Found"));
    }
    @ExceptionHandler(DataApiMapperException.class)
    public ResponseEntity<ExceptionResponse> handleDataApiMapperException(DataApiMapperException e) {
        log.error("data api mapper error is : {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Data API Mapper Error"));
    }
    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ExceptionResponse> handlePaymentException(PaymentException e) {
        log.error("payment error is : {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value(), "Payment Error")
        );
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handlePaymentNotFoundException(PaymentNotFoundException e) {
        log.error("payment not found error is : {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ExceptionResponse(e.getMessage(), HttpStatus.NOT_FOUND.value(), "Payment Failed")
        );
    }
    @ExceptionHandler(CouponApplicationException.class)
    public ResponseEntity<ExceptionResponse> handleCouponApplicationException(CouponApplicationException e) {
        log.error("coupon application error is : {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value(), "Coupon Application Error")
        );
    }
    @ExceptionHandler(CouponDomainException.class)
    public ResponseEntity<ExceptionResponse> handleCouponDomainException(CouponDomainException e) {
        log.error("coupon domain error is : {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value(), "Coupon Domain Error")
        );
    }
    @ExceptionHandler(CouponUsageNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleCouponUsageNotFoundException(CouponUsageNotFoundException e) {
        log.error("coupon usage not found error is : {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ExceptionResponse(e.getMessage(), HttpStatus.NOT_FOUND.value(), "Coupon Usage Not Found")
        );
    }
}
