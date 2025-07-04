package shop.shportfolio.user.infrastructure.database.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.shportfolio.user.infrastructure.database.jpa.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserEntity, Integer> {


    Optional<UserEntity> findUserEntityByUserId(UUID userId);

    Optional<UserEntity> findUserEntityByUsername(String username);

    Optional<UserEntity> findUserEntityByEmail(String email);

    Optional<UserEntity> findUserEntityByPhoneNumber(String phoneNumber);
}
