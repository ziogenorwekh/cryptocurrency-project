package shop.shportfolio.user.application.ports.output.mail;

public interface MailSenderAdapter {
    void sendMailForResetPassword(String email,String token);

    void sendMailForTempEmailAuth(String email, String code);
}
