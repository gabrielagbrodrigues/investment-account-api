package investment_account_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI investmentAccountOpenAPI(){

        return new OpenAPI()
                .info(new Info()
                .title("Investment account API")
                .description(
                        "API para gerenciamento de conta corrente virtual de investimentos, " +
                                "com controle de movimentações financeira, bloquei preventivo " +
                                "de valores e liquidação de operações."
                )
                .version("1.0.0")
                );
    }
}
