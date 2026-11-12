package investment_account_api.account.service;

import investment_account_api.account.entity.InvestmentAccount;
import investment_account_api.account.repository.InvestmentAccountRepository;
import investment_account_api.account.enums.AccountStatus;
import investment_account_api.transaction.enums.TransactionType;
import investment_account_api.transaction.service.TransactionService;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;


@Service
public class InvestmentAccountService {

    private final InvestmentAccountRepository investmentAccountRepository;
    private final TransactionService transactionService;

    public InvestmentAccountService(InvestmentAccountRepository investmentAccountRepositoroy,
                                    TransactionService transactionService){
        this.investmentAccountRepository = investmentAccountRepositoroy;
        this.transactionService = transactionService;
    }

    public InvestmentAccount openAccount(String customerId){

        String accountNumber = UUID.randomUUID()
                .toString()
                .replace("-","-")
                .substring(0, 20);
        InvestmentAccount account =
                new InvestmentAccount(accountNumber, customerId);

        return investmentAccountRepository.save(account);
    }

    public InvestmentAccount activateAccount(Long accountId){

        InvestmentAccount account = investmentAccountRepository
                .findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Conta de investimento não encontrada."
                ));
        if (account.getStatus() != AccountStatus.PENDDING_ACTIVATION){
            throw  new IllegalStateException(
                    "A conta não está aguardando ativação."
            );
        }
        account.activate();
        return investmentAccountRepository.save(account);
    }

    public InvestmentAccount findAccountById(Long accountId){
        return investmentAccountRepository
                .findById(accountId)
                .orElseThrow(()-> new IllegalArgumentException(
                        "Conta de investimento não encontrada"
                ));
    }

    @Transactional
    public InvestmentAccount deposit(Long accountId, BigDecimal amount){

        InvestmentAccount account = investmentAccountRepository
                .findById(accountId)
                .orElseThrow(()-> new IllegalArgumentException(
                        "Conta de investimento não encontrada."));
        if (account.getStatus() != AccountStatus.ACTIVE){
            throw new IllegalStateException(
                    "A conta precisa estar ativa para receber depositos."
            );
        }
        account.credit(amount);

        InvestmentAccount savedAccount =
                investmentAccountRepository.save(account);

        transactionService.createTransaction(
                accountId,
                TransactionType.DEPOSIT,
                amount,
                UUID.randomUUID().toString(),
                LocalDate.now(),
                LocalDate.now()
        );
        return savedAccount;
    }
}
