package shop.shportfolio.user.database.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.shportfolio.user.database.jpa.entity.UserEntity;

public interface UserJpaRepository extends JpaRepository<UserEntity, Integer> {
}
