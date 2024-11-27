package dev.pz.airportlpnu;

import dev.pz.airportlpnu.services.email.EmailService;
import dev.pz.airportlpnu.services.email.templates.EmailTemplate;
import dev.pz.airportlpnu.services.email.templates.TemplateProcessingService;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateProcessingService templateProcessingService;

    @Mock
    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendEmail_Success() throws Exception {
        // Arrange
        String to = "test@example.com";
        String subject = "Test Subject";
        String generatedContent = "<h1>Test Email Content</h1>";
        EmailTemplate template = mock(EmailTemplate.class);

        when(templateProcessingService.generateContent(template)).thenReturn(generatedContent);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        emailService.sendEmail(to, subject, template);

        // Assert
        verify(mailSender, times(1)).send(mimeMessage);
        verify(mimeMessage).setSubject(eq(subject)); // Verifying subject
        verify(mimeMessage).setRecipient(eq(MimeMessage.RecipientType.TO), eq(new InternetAddress(to))); // Verifying recipient
    }

    @Test
    void testSendEmail_TemplateProcessingFailure() {
        // Arrange
        String to = "test@example.com";
        String subject = "Test Subject";
        EmailTemplate template = mock(EmailTemplate.class);

        when(templateProcessingService.generateContent(template)).thenThrow(new RuntimeException("Template processing failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                emailService.sendEmail(to, subject, template));
        assertEquals("Template processing failed", exception.getMessage());
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void testSendEmail_MessagingException() {
        // Arrange
        String to = "test@example.com";
        String subject = "Test Subject";
        String generatedContent = "<h1>Test Email Content</h1>";
        EmailTemplate template = mock(EmailTemplate.class);

        when(templateProcessingService.generateContent(template)).thenReturn(generatedContent);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("SMTP error")).when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                emailService.sendEmail(to, subject, template));
        assertTrue(exception.getMessage().contains("SMTP error"));
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

}
