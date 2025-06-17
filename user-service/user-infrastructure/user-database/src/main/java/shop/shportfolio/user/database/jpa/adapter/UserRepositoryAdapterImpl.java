package shop.shportfolio.user.database.jpa.adapter;

import org.springframework.stereotype.Repository;
import shop.shportfolio.user.application.ports.output.repository.UserRepositoryAdaptor;
import shop.shportfolio.user.database.jpa.repository.UserJpaRepository;
import shop.shportfolio.user.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryAdapterImpl implements UserRepositoryAdaptor {

    private final UserJpaRepository userJpaRepository;

    public UserRepositoryAdapterImpl(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Optional<User> findByUserId(UUID userId) {
        return Optional.empty();
    }

    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return Optional.empty();
    }

    @Override
    public void deleteUserById(UUID userId) {

    }
}
