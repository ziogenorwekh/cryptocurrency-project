package shop.shportfolio.user.database.test;


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import shop.shportfolio.user.database.jpa.adapter.UserRepositoryAdapterImpl;
import shop.shportfolio.user.database.jpa.adapter.UserTrHistoryRepositoryPortImpl;
import shop.shportfolio.user.database.jpa.entity.RoleEntity;
import shop.shportfolio.user.database.jpa.entity.SecuritySettingsEntity;
import shop.shportfolio.user.database.jpa.entity.TransactionHistoryEntity;
import shop.shportfolio.user.database.jpa.entity.UserEntity;
import shop.shportfolio.user.database.jpa.mapper.UserDataAccessMapper;
import shop.shportfolio.user.database.jpa.repository.TransactionHistoryJpaRepository;
import shop.shportfolio.user.database.jpa.repository.UserJpaRepository;

@EntityScan(basePackageClasses = {UserEntity.class, RoleEntity.class, SecuritySettingsEntity.class,
        TransactionHistoryEntity.class})
@EnableJpaRepositories(basePackages = "shop.shportfolio.user.database")
@Configuration
public class JpaTestConfiguration {


    @Bean
    public UserRepositoryAdapterImpl userRepositoryAdapter(UserJpaRepository userJpaRepository) {
        return new UserRepositoryAdapterImpl(userJpaRepository,userDataAccessMapper());
    }

    @Bean
    public UserDataAccessMapper userDataAccessMapper() {
        return new UserDataAccessMapper();
    }

    @Bean
    public UserTrHistoryRepositoryPortImpl userTrHistoryRepositoryAdapter(TransactionHistoryJpaRepository tHistoryJpaRepository) {
        return new UserTrHistoryRepositoryPortImpl(tHistoryJpaRepository,userDataAccessMapper());
    }
}
