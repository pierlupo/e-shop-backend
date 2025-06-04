package com.eShop.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipient;

    private String subject;

    @Column(length = 5000)
    private String body;

    private boolean success;

    private String errorMessage;

    private LocalDateTime sentAt;
}