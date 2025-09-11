package shop.shportfolio.user.infrastructure.email.adapter;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import shop.shportfolio.user.application.exception.mail.CustomMailSendException;
import shop.shportfolio.user.application.ports.output.mail.MailSenderPort;
import shop.shportfolio.user.infrastructure.email.configuration.APIUrlData;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class MailSenderAdapter implements MailSenderPort {

    private final JavaMailSender javaMailSender;
    private final APIUrlData apiUrlData;

    @Autowired
    public MailSenderAdapter(JavaMailSender javaMailSender, APIUrlData apiUrlData) {
        this.javaMailSender = javaMailSender;
        this.apiUrlData = apiUrlData;
    }

    @Async
    @Override
    public void sendMailForResetPassword(String email, String token) {
        log.info("Sending reset password mail to {}", email);
        sendEmail(email, "Reset Your Password", buildResetPasswordContent(token));
    }

    @Async
    @Override
    public void sendMailWithEmailAndCode(String email, String code) {
        log.info("Sending activation mail to {}", email);
        sendEmail(email, "Activate Your Account", buildActivationContent(code));
    }

    @Async
    @Override
    public void sendMailWithEmailAnd2FACode(String email, String code) {
        log.info("Sending 2FA code to {}", email);
        sendEmail(email, "Your 2FA Login Code", build2FAContent(code));
    }

    private void sendEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new CustomMailSendException("Failed to send email", e);
        }
    }

    private String buildResetPasswordContent(String token) {
        String resetPwdUrl = apiUrlData.getResetPasswordUrl();
        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        String fullUrl = resetPwdUrl + "?token=" + encodedToken;
        return "<div style=\"font-family: Arial, sans-serif; margin: 20px;\">" +
                "<h2>Reset Your Password</h2>" +
                "<p>We received a request to reset your password.</p>" +
                "<p>Please click the link below to reset it:</p>" +
                "<a href=\"" + fullUrl + "\">Reset Password</a>" +
                "<p>If you didn’t request a password reset, you can ignore this email.</p>" +
                "<p>Best regards,<br>Your Service Team</p>" +
                "</div>";
    }

    private String buildActivationContent(String code) {
        return "<div style=\"font-family: Arial, sans-serif; margin: 20px;\">" +
                "<h2>Activate Your Account</h2>" +
                "<p>Thank you for signing up. Use the following code to activate your account:</p>" +
                "<h3>" + code + "</h3>" +
                "<p>If you didn't sign up, please ignore this email.</p>" +
                "<p>Best regards,<br>Your Service Team</p>" +
                "</div>";
    }

    private String build2FAContent(String code) {
        return "<div style=\"font-family: Arial, sans-serif; margin: 20px;\">" +
                "<h2>Two-Factor Authentication Code</h2>" +
                "<p>Use the code below to complete your login:</p>" +
                "<h3>" + code + "</h3>" +
                "<p>If you did not try to login, please secure your account.</p>" +
                "<p>Best regards,<br>Your Service Team</p>" +
                "</div>";
    }
}
