package investment_account_api.account.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record DepositRequest(
        @JsonProperty("valorDoDeposito")
        BigDecimal amount) {
}
