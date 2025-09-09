package shop.shportfolio.user.infrastructure.database.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Table(name = "USER_ENTITY")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @Column(name = "USER_ID", unique = true, nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "USERNAME", nullable = false, unique = true)
    private String username;

    @Column(name = "ENCODED_PASSWORD", nullable = false)
    private String encodedPassword;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Embedded
    private ProfileImageEmbedded profileImageEmbedded;

    @Setter
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<RoleEntity> roles = new ArrayList<>();

    @Setter
    @Builder.Default
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private SecuritySettingsEntity securitySettingsEntity = new SecuritySettingsEntity();

}
