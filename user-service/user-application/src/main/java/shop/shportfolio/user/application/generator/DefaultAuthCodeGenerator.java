package shop.shportfolio.user.application.generator;

import org.springframework.stereotype.Component;

@Component
public class DefaultAuthCodeGenerator implements AuthCodeGenerator {
    public String generate() {
        int code = (int)(Math.random()*900000) + 100000;
        return String.valueOf(code);
    }
}
