package investment_account_api.statement.service;


import investment_account_api.statement.dto.StatementResponse;
import investment_account_api.transaction.entity.Transaction;
import investment_account_api.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StatementService {

    private final TransactionRepository transactionRepository;

    public StatementService(TransactionRepository transactionRepository){

        this.transactionRepository = transactionRepository;
    }

    public List<StatementResponse> getStatement(Long accountId){

        List<Transaction> transactions =
                transactionRepository
                        .findByInvestmentAccount_IdOrderByTransactionDateDescIdDesc(accountId);

        return convertToResponse(transactions);
    }

    public List<StatementResponse> getStatementByPeriod(
            Long accountId,
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate.isAfter(endDate)){
            throw new IllegalArgumentException(
                    "A data inicial não pode ser posterior à data final."
            );
        }

        List<Transaction> transactions =
                transactionRepository
                        .findTransactionsByAccountAndPeriod(
                                accountId,
                                startDate,
                                endDate
                        );

        return convertToResponse(transactions);
    }

    private List<StatementResponse> convertToResponse(
            List<Transaction> transactions){

        return transactions.stream()
                .map(transaction -> new StatementResponse(
                        transaction.getId(),
                        transaction.getInvestmentAccount().getId(),
                        transaction.getType(),
                        transaction.getStatus(),
                        transaction.getAmount(),
                        transaction.getTransactionDate(),
                        transaction.getSettlementDate()
                ))
                .toList();
    }
}
