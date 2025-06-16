package shop.shportfolio.user.application.ports.output.repository;

import shop.shportfolio.user.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryAdaptor {


    Optional<User> findByUserId(UUID userId);

    User save(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByPhoneNumber(String phoneNumber);

    void deleteUserById(UUID userId);
}
