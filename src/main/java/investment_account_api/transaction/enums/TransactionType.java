package investment_account_api.transaction.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TransactionType {

    @JsonProperty("COMPRA")
    PURCHASE,

    @JsonProperty("VENDA")
    SALE,

    @JsonProperty("DEPOSITO")
    DEPOSIT
}
