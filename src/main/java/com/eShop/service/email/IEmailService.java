package com.eShop.service.email;

import jakarta.mail.MessagingException;

public interface IEmailService {

    void sendEmail(String to, String subject, String text) throws MessagingException;

}