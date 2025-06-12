package shop.shportfolio.user.application.ports.output.repository;

import shop.shportfolio.user.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryAdapter {


    Optional<User> findByUserId(UUID userId);

    Optional<User> save(User user);
}
