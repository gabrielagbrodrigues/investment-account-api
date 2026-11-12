package investment_account_api.transaction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import investment_account_api.transaction.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransactionRequest (

        @JsonProperty("tipoDaOperacao")
        TransactionType type,

        @JsonProperty("valorDaOperacao")
        BigDecimal amount,

        @JsonProperty("chaveDaRequisicao")
        String idempotencyKey,

        @JsonProperty("dataDaOperacao")
        LocalDate transactionDate,

        @JsonProperty("dataPrevistaLiquidacao")
        LocalDate settlementDate
) {
}
