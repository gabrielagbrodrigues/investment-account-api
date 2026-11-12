package investment_account_api.account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountBalanceResponse(
        @JsonProperty("saldo Atual")
        BigDecimal currentBalance,

        @JsonProperty("debitosPendentes")
        BigDecimal pendingDebits,

        @JsonProperty("dataPrevistaLiquidacaoDebitosPendentes")
        LocalDate pendingDebitsSettlementDate,

        @JsonProperty("creditosPendentes")
        BigDecimal pendingCredits,

        @JsonProperty("dataPrevistaLiquidacaoCreditosPendentes")
        LocalDate pendingCreditsSettlementDate,

        @JsonProperty("saldoProjetado")
        BigDecimal projectsBalance,

        @JsonProperty("saldoProjetadoD10")
        BigDecimal projectedBalanceD10,

        @JsonProperty("totalCompras")
        BigDecimal totalPurchases,

        @JsonProperty("totalVendas")
        BigDecimal totalSales,

        @JsonProperty("valorUltimoDebito")
        BigDecimal lastDebitAmount,

        @JsonProperty("dataUltimoDebito")
        LocalDate lastDebitDate,

        @JsonProperty("valorUltimoCredito")
        BigDecimal lastCreditAmount,

        @JsonProperty("dataDoUltimoCredito")
        LocalDate lastCreditDate

) {
}
