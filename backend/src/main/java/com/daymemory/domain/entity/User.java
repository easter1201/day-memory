package com.daymemory.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column
    private String profileImageUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean emailVerified = false;

    @Enumerated(EnumType.STRING)
    @Column
    private OAuthProvider oauthProvider;

    @Column
    private String oauthProviderId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Event> events = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GiftItem> giftItems = new ArrayList<>();

    public void verifyEmail() {
        this.emailVerified = true;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}
