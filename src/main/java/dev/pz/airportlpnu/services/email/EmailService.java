package dev.pz.airportlpnu.services.email;

import dev.pz.airportlpnu.services.email.templates.EmailTemplate;
import dev.pz.airportlpnu.services.email.templates.TemplateProcessingService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateProcessingService templateProcessingService;

    public EmailService(JavaMailSender mailSender, TemplateProcessingService templateProcessingService) {
        this.mailSender = mailSender;
        this.templateProcessingService = templateProcessingService;
    }

    public void sendEmail(String to, String subject, EmailTemplate template) {
        String content = templateProcessingService.generateContent(template);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("airport-lpnu-app@ukr.net");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
