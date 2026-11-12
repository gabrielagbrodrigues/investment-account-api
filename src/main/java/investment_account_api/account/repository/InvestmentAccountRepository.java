package investment_account_api.account.repository;

import investment_account_api.account.entity.InvestmentAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestmentAccountRepository
    extends JpaRepository<InvestmentAccount, Long>{

    }

