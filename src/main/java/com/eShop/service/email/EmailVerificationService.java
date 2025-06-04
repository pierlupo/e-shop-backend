package com.eShop.service.email;

import com.eShop.exceptions.MessagingException;
import com.eShop.model.EmailVerificationToken;
import com.eShop.model.User;
import com.eShop.repository.EmailVerificationTokenRepository;
import com.eShop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;


    @Transactional
    public EmailVerificationToken createOrReplaceToken(User user) {
        log.info("Email verification token create");
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

    @Transactional
    public void deleteToken(EmailVerificationToken token) {
        tokenRepository.delete(token);
    }

    @Transactional
    public EmailVerificationToken createAndStoreToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return createOrReplaceToken(user);
    }

    public void sendVerificationEmail(User user, String token) {
        try {
            emailService.sendVerificationEmail(user, token);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }
}