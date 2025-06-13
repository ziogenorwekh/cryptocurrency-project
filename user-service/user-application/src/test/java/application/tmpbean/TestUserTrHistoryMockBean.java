package application.tmpbean;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.shportfolio.user.application.TransactionHistoryApplicationService;
import shop.shportfolio.user.application.TransactionHistoryApplicationServiceImpl;
import shop.shportfolio.user.application.handler.UserTransactionHistoryQueryHandler;
import shop.shportfolio.user.application.mapper.UserDataMapper;
import shop.shportfolio.user.application.ports.output.repository.UserTrHistoryRepositoryAdapter;

@Configuration
public class TestUserTrHistoryMockBean {

    @Bean
    public UserTransactionHistoryQueryHandler userTransactionHistoryQueryHandler() {
        return new UserTransactionHistoryQueryHandler(userTrHistoryRepositoryAdapter());
    }

    @Bean
    public TransactionHistoryApplicationService transactionHistoryApplicationService() {
        return new TransactionHistoryApplicationServiceImpl(userTransactionHistoryQueryHandler(), userDataMapper());
    }


    @Bean
    public UserDataMapper userDataMapper() {
        return new  UserDataMapper();
    }
    @Bean
    public UserTrHistoryRepositoryAdapter userTrHistoryRepositoryAdapter() {
        return Mockito.mock(UserTrHistoryRepositoryAdapter.class);
    }
}
