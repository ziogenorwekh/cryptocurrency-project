package shop.shportfolio.user.application.ports.output.mail;

public interface MailSenderAdapter {
    void sendMailForResetPassword(String email,String token);
    void sendMailWithEmailAndCode(String email, String code);
}
