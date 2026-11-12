package investment_account_api.transaction.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TransactionStatus {

    @JsonProperty("PENDENTE")
    PENDING,

    @JsonProperty("LIQUIDADA")
    SETTLED,

    @JsonProperty("CANCELADA")
    CANCELLED

}
