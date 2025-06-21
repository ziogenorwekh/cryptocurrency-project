package shop.shportfolio.user.database.jpa.adapter;

import org.springframework.stereotype.Repository;
import shop.shportfolio.user.application.ports.output.repository.UserRepositoryAdaptor;
import shop.shportfolio.user.database.jpa.entity.UserEntity;
import shop.shportfolio.user.database.jpa.exception.UserDataAccessException;
import shop.shportfolio.user.database.jpa.mapper.UserDataAccessMapper;
import shop.shportfolio.user.database.jpa.repository.UserJpaRepository;
import shop.shportfolio.user.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryAdapterImpl implements UserRepositoryAdaptor {

    private final UserJpaRepository userJpaRepository;
    private final UserDataAccessMapper userDataAccessMapper;

    public UserRepositoryAdapterImpl(UserJpaRepository userJpaRepository,
                                     UserDataAccessMapper userDataAccessMapper) {
        this.userJpaRepository = userJpaRepository;
        this.userDataAccessMapper = userDataAccessMapper;
    }

    @Override
    public Optional<User> findByUserId(UUID userId) {
        return userJpaRepository.findUserEntityByUserId(userId).map(userDataAccessMapper::userEntityToUser);
    }

    @Override
    public User save(User user) {
        UserEntity userEntity = userDataAccessMapper.userToUserEntity(user);
        UserEntity saved = userJpaRepository.save(userEntity);
        return userDataAccessMapper.userEntityToUser(saved);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findUserEntityByEmail(email).map(userDataAccessMapper::userEntityToUser);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findUserEntityByUsername(username).map(userDataAccessMapper::userEntityToUser);
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userJpaRepository.findUserEntityByPhoneNumber(phoneNumber).map(userDataAccessMapper::userEntityToUser);
    }

    @Override
    public void deleteUserById(UUID userId) {
        UserEntity userEntityByUserId = userJpaRepository.findUserEntityByUserId(userId)
                .orElseThrow(() -> new UserDataAccessException(String.format("User with id %s not found", userId)));
        userJpaRepository.delete(userEntityByUserId);
    }
}
