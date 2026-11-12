package investment_account_api.account.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AccountStatus {
    @JsonProperty("PENDENTE_ATIVACAO")
    PENDDING_ACTIVATION,

    @JsonProperty("ATIVO")
    ACTIVE,
    BLOCKED,
    CLOSED
}
