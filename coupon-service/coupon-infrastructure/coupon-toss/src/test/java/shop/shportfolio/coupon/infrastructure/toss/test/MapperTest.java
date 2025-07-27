package shop.shportfolio.coupon.infrastructure.toss.test;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shop.shportfolio.common.domain.dto.payment.PaymentResponse;
import shop.shportfolio.coupon.infrastructure.toss.mapper.CouponDataApiMapper;

@SpringBootTest(classes = BeanConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class MapperTest {

    @Autowired
    private CouponDataApiMapper couponDataApiMapper;

    private final String json = """
            {
              "mId": "tosspayments",
              "lastTransactionKey": "9C62B18EEF0DE3EB7F4422EB6D14BC6E",
              "paymentKey": "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
              "orderId": "a4CWyWY5m89PNh7xJwhk1",
              "orderName": "토스 티셔츠 외 2건",
              "taxExemptionAmount": 0,
              "status": "DONE",
              "requestedAt": "2024-02-13T12:17:57+09:00",
              "approvedAt": "2024-02-13T12:18:14+09:00",
              "useEscrow": false,
              "cultureExpense": false,
              "card": {
                "issuerCode": "71",
                "acquirerCode": "71",
                "number": "12345678****000*",
                "installmentPlanMonths": 0,
                "isInterestFree": false,
                "interestPayer": null,
                "approveNo": "00000000",
                "useCardPoint": false,
                "cardType": "신용",
                "ownerType": "개인",
                "acquireStatus": "READY",
                "amount": 1000
              },
              "virtualAccount": null,
              "transfer": null,
              "mobilePhone": null,
              "giftCertificate": null,
              "cashReceipt": null,
              "cashReceipts": null,
              "discount": null,
              "cancels": null,
              "secret": null,
              "type": "NORMAL",
              "easyPay": {
                "provider": "토스페이",
                "amount": 0,
                "discountAmount": 0
              },
              "country": "KR",
              "failure": null,
              "isPartialCancelable": true,
              "receipt": {
                "url": "https://dashboard.tosspayments.com/receipt/redirection?transactionId=tviva20240213121757MvuS8&ref=PX"
              },
              "checkout": {
                "url": "https://api.tosspayments.com/v1/payments/5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1/checkout"
              },
              "currency": "KRW",
              "totalAmount": 1000,
              "balanceAmount": 1000,
              "suppliedAmount": 909,
              "vat": 91,
              "taxFreeAmount": 0,
              "metadata": null,
              "method": "카드",
              "version": "2022-11-16"
            }
            """;


    @Test
    @DisplayName("매핑이 정상적으로 작동하는지 테스트")
    public void mapperToJsonTest() {
        // given && when
        PaymentResponse paymentResponse = couponDataApiMapper.toPaymentResponse(json);
        // then
        Assertions.assertNotNull(paymentResponse);
        Assertions.assertEquals("5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1", paymentResponse.getPaymentKey());
        Assertions.assertEquals("토스 티셔츠 외 2건",paymentResponse.getDescription());
    }
}
