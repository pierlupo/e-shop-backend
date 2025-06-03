package com.eShop.service.email;

import com.eShop.model.User;
import com.eShop.model.EmailLog;
import com.eShop.repository.EmailLogRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService{

    private final JavaMailSender mailSender;
    private final EmailLogRepository emailLogRepository;

    @Value("${app.frontend.url}")
    private String frontendUrl;


    @Override
    public void sendEmail(String to, String subject, String text) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, false);

        mailSender.send(message);

    }

    @Override
    public void sendVerificationEmail(User user, String token) {
        String subject = "Verify your email";
        String verificationUrl = frontendUrl + "/verify-email?token=" + token;
        String body = String.format(
                "Hello %s,\n\nPlease click the link below to verify your email:\n%s\n\nThis link will expire in 24 hours.\n\nThank you.",
                user.getFirstname(), verificationUrl
        );
        EmailLog.EmailLogBuilder logBuilder = EmailLog.builder()
                .recipient(user.getEmail())
                .subject(subject)
                .body(body)
                .sentAt(LocalDateTime.now());
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            logBuilder.success(true);
        } catch (Exception e) {
            logBuilder.success(false).errorMessage(e.getMessage());
        }
        emailLogRepository.save(logBuilder.build());
    }
}
