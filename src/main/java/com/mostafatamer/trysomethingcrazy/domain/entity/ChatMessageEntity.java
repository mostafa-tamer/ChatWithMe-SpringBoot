package com.mostafatamer.trysomethingcrazy.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@AllArgsConstructor
@ToString
@NoArgsConstructor
@Table(name = "messages")
public class ChatMessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @Column(nullable = false)
    String message;

    @Column(nullable = false)
    String senderUsername;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timeStamp;

    Long messageNumber;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(nullable = false)
    ChatEntity chat;

    @PrePersist
    public void prePersist() {
        this.timeStamp = LocalDateTime.now();
    }
}
