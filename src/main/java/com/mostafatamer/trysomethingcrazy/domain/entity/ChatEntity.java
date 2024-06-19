package com.mostafatamer.trysomethingcrazy.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "chats")
public class ChatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @Column(nullable = false, unique = true)
    String tag;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "chat")
    List<ChatMessageEntity> messages;

    @ManyToMany(fetch = FetchType.EAGER)
    List<UserEntity> members; // they must be exactly 2
}
