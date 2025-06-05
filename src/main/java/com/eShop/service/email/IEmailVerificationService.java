package com.eShop.service.email;

import com.eShop.model.EmailVerificationToken;
import com.eShop.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface IEmailVerificationService {

    @Transactional
    EmailVerificationToken createOrReplaceToken(User user);

    Optional<EmailVerificationToken> getToken(String token);

    boolean isTokenExpired(EmailVerificationToken token);

    @Transactional
    void deleteToken(EmailVerificationToken token);

    @Transactional
    EmailVerificationToken createAndStoreToken(Long userId);

    void sendVerificationEmail(User user, String token);
}
