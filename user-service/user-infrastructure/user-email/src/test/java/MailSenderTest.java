import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import shop.shportfolio.user.email.adapter.MailSenderAdapterImpl;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
public class MailSenderTest {
    private JavaMailSender javaMailSender;
    private MailSenderAdapterImpl mailSenderAdapter;

    @BeforeEach
    void setUp() {
        javaMailSender = Mockito.mock(JavaMailSender.class);
        mailSenderAdapter = new MailSenderAdapterImpl(javaMailSender);
    }

    @Test
    void sendMailForResetPassword_shouldSendMail() {
        // given
        MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
        Mockito.when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        // when
        mailSenderAdapter.sendMailForResetPassword("test@example.com", "reset-token");
        // then
        Mockito.verify(javaMailSender, Mockito.times(1)).send(Mockito.any(MimeMessage.class));
    }

    @Test
    void sendMailWithEmailAndCode_shouldSendMail() {
        // given
        MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
        Mockito.when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        // when
        mailSenderAdapter.sendMailWithEmailAndCode("user@example.com", "activation-code");
        // then
        Mockito.verify(javaMailSender, Mockito.times(1)).send(Mockito.any(MimeMessage.class));
    }

    @Test
    void sendMailWithEmailAnd2FACode_shouldSendMail() {
        // given
        MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
        Mockito.when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        // when
        mailSenderAdapter.sendMailWithEmailAnd2FACode("user@example.com", "2fa-code");
        // then
        Mockito.verify(javaMailSender, Mockito.times(1)).send(Mockito.any(MimeMessage.class));
    }
}
