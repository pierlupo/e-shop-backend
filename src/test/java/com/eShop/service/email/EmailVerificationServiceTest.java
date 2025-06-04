package com.eShop.service.email;

import com.eShop.model.EmailVerificationToken;
import com.eShop.model.User;
import com.eShop.repository.EmailVerificationTokenRepository;
import com.eShop.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class EmailVerificationServiceTest {

    @Mock
    private EmailVerificationTokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailVerificationService emailVerificationService;

    @Test
    void testCreateAndSendToken() throws Exception {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setFirstname("Alice");
        user.setEmail("alice@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(tokenRepository.save(any(EmailVerificationToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        emailVerificationService.createAndSendToken(userId);

        // Then
        verify(emailService).sendVerificationEmail(eq(user), anyString());
        verify(tokenRepository).save(any(EmailVerificationToken.class));
    }
}