package investment_account_api.transaction.dto;

public record TransactionCreationResponse(
        String message,
        TransactionResponse transaction
) {
}
