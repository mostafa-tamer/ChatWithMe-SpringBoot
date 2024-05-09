package com.mostafatamer.trysomethingcrazy.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@AllArgsConstructor
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

    @PrePersist
    public void prePersist() {
        this.timeStamp = LocalDateTime.now();
    }

    @ManyToOne
    @JoinColumn(nullable = false)
    ChatEntity chat;
}
