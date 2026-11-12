package investment_account_api.statement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import investment_account_api.transaction.enums.TransactionStatus;
import investment_account_api.transaction.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StatementResponse(

        @JsonProperty("identificador")
        Long id,

        @JsonProperty("contaId")
        Long accountId,

        @JsonProperty("tipoDaOperacao")
        TransactionType type,

        @JsonProperty("situacaoDaOperacao")
        TransactionStatus status,

        @JsonProperty("valorDaoperacao")
        BigDecimal amount,

        @JsonProperty("dataDaoperacao")
        LocalDate transactionDate,

        @JsonProperty("dataPrevistaLiquidacao")
        LocalDate settlementDate

) {
}
