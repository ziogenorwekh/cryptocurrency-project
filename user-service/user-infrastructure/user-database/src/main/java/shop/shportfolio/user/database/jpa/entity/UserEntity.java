package shop.shportfolio.user.database.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.shportfolio.user.domain.entity.SecuritySettings;

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

    @Column(name = "PROFILE_IMAGE_ID",nullable = false,columnDefinition = "BINARY(16)")
    private UUID profileImageId;

    @Column(name = "FILE_URL")
    private String fileUrl;

    @Column(name = "PROFILE_IMAGE_EXTENSION")
    private String profileImageExtensionWithName;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<RoleEntity> roles = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private SecuritySettingsEntity securitySettingsEntity;

}
