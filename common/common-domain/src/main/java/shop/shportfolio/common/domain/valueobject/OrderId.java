package shop.shportfolio.common.domain.valueobject;

import java.util.UUID;

public class OrderId extends BaseId<String>{
    public OrderId(String value) {
        super(value);
    }


    public static OrderId anonymous() {
        return new OrderId("anonymous-" + UUID.randomUUID());
    }
    @Override
    public String getValue() {
        return super.getValue();
    }
}
