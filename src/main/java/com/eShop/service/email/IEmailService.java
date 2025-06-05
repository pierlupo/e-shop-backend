package com.eShop.service.email;

import com.eShop.model.User;
import jakarta.mail.MessagingException;

public interface IEmailService {

    void sendEmail(String to, String subject, String text) throws MessagingException;

    void sendVerificationEmail(User user, String token);

    void sendPasswordResetEmail(User user, String token);
}