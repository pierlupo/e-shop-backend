package com.eShop.service.email;

import com.eShop.exceptions.MessagingException;
import com.eShop.model.EmailVerificationToken;
import com.eShop.model.User;
import com.eShop.repository.EmailVerificationTokenRepository;
import com.eShop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public EmailVerificationToken createToken(User user) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusDays(1);
        EmailVerificationToken emailToken = new EmailVerificationToken(token, user, expiry);
        return tokenRepository.save(emailToken);
    }

    public Optional<EmailVerificationToken> getToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public boolean isTokenExpired(EmailVerificationToken token) {
        return token.getExpiryDate().isBefore(LocalDateTime.now());
    }

    public void deleteToken(EmailVerificationToken token) {
        tokenRepository.delete(token);
    }

    public void createAndSendToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        EmailVerificationToken token = createToken(user);
        String verificationLink = frontendUrl + "/verify-email?token=" + token.getToken();

        String subject = "Verify your email address";
        String message = String.format(
                "Hello %s,\n\nPlease click the link below to verify your email:\n%s\n\nThis link will expire in 24 hours.\n\nThank you.",
                user.getFirstname(), verificationLink
        );
        try {
            emailService.sendEmail(user.getEmail(), subject, message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email", e);
        } catch (jakarta.mail.MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
