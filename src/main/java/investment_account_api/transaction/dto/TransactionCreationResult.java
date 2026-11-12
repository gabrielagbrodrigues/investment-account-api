package investment_account_api.transaction.dto;

import investment_account_api.transaction.entity.Transaction;

public record TransactionCreationResult(
        Transaction transaction,
        boolean reused
) {
}
