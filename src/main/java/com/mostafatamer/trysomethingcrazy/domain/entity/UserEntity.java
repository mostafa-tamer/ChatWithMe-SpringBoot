package com.mostafatamer.trysomethingcrazy.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class UserEntity implements UserDetails, Principal {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    String nickname;

    @Column(unique = true, nullable = false)
    String username;

    @Column(nullable = false)
    String password;

    String firebaseToken;

    @ToString.Exclude
    @ManyToMany(cascade = CascadeType.ALL)
    List<GroupEntity> groups;

    @ToString.Exclude
    @ManyToMany(cascade = CascadeType.ALL)
    List<UserEntity> friends;

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<FriendRequestEntity> friendRequests;

    @ToString.Exclude
    @ManyToMany(mappedBy = "members", cascade = CascadeType.ALL)
    List<ChatEntity> chats;

    @ToString.Exclude
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<RoleEntity> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return username;
    }
}
