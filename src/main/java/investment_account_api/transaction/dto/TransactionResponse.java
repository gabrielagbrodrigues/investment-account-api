package investment_account_api.transaction.dto;

import investment_account_api.transaction.enums.TransactionStatus;
import investment_account_api.transaction.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponse(
        Long id,
        Long accountId,
        TransactionType type,
        TransactionStatus status,
        BigDecimal amount,
        LocalDate transactionDate,
        LocalDate settlementDate
) {
}
