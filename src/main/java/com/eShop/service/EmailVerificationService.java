package com.eShop.service;

import com.eShop.model.EmailVerificationToken;
import com.eShop.model.User;
import com.eShop.repository.EmailVerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;

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
}
