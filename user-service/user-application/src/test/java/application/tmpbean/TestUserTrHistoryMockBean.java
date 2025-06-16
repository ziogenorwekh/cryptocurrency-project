package application.tmpbean;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.shportfolio.user.application.ports.input.TransactionHistoryApplicationService;
import shop.shportfolio.user.application.TransactionHistoryApplicationServiceImpl;
import shop.shportfolio.user.application.handler.UserTrHistoryCommandHandler;
import shop.shportfolio.user.application.mapper.UserDataMapper;
import shop.shportfolio.user.application.ports.output.repository.UserRepositoryAdaptor;
import shop.shportfolio.user.application.ports.output.repository.UserTrHistoryRepositoryAdapter;
import shop.shportfolio.user.domain.TrHistoryDomainService;
import shop.shportfolio.user.domain.TrHistoryDomainServiceImpl;

@Configuration
public class TestUserTrHistoryMockBean {

    @Bean
    public UserTrHistoryCommandHandler userTransactionHistoryQueryHandler() {
        return new UserTrHistoryCommandHandler(userTrHistoryRepositoryAdapter(),userDataRepositoryAdaptor()
        ,trHistoryDomainService());
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

    @Bean
    public UserRepositoryAdaptor userDataRepositoryAdaptor() {
        return Mockito.mock(UserRepositoryAdaptor.class);
    }

    @Bean
    public TrHistoryDomainService trHistoryDomainService() {
        return new TrHistoryDomainServiceImpl();
    }
}
