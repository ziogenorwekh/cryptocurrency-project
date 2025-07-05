package shop.shportfolio.user.application.ports.output.mail;

public interface MailSenderPort {
    void sendMailForResetPassword(String email,String token);
    void sendMailWithEmailAndCode(String email, String code);
    void sendMailWithEmailAnd2FACode(String email, String code);
}
