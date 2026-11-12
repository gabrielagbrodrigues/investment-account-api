package investment_account_api.statement.controller;

import investment_account_api.statement.dto.StatementResponse;
import investment_account_api.statement.service.StatementService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/investment-accounts")
@Tag(
        name = "Extrato de Movimentações",
        description = "Consulta do histórico de movimentações financeiras das contas de investimento."
)
public class StatementController {

    private final StatementService statementService;

    public StatementController(StatementService statementService){
        this.statementService = statementService;
    }

    @GetMapping("/{accountId}/statement")
    @Operation(
            summary = "Consulta extrato completo",
            description = "Retorna todas as movimentações financeiras da contas, " +
                    "incluindo operações pendentes e liquidadas, ordenadas de mais recente para a mais antiga."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Extrato completo com sucesso."
    )
    public List<StatementResponse> getStatement(

            @Parameter(
                    description = "Identificador da conta de investimento.",
                    required = true
            )
            @PathVariable Long accountId){

        return statementService.getStatement(accountId);
    }

    @GetMapping("/{accountId}/statement/period")
    @Operation(
            summary = "Consultar extrato por período",
            description = "Retorna as movimentações financeiras da conta dentro do intervalo de datas informado, " +
                    "incluindo as datas inicial e final"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Extrato do período retornado com sucesso."
            )
    })
    public List<StatementResponse> getStatementByPeriod(

            @Parameter(
                    description = "Identificador da conta de investimento.",
                    required = true
            )
            @PathVariable Long accountId,

            @Parameter(
                    description = "Data inicial do período. Formato: AAAA-MM-DD.",
                    required = true,
                    example = "2026-11-10"
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @Parameter(
                    description = "Data final do período. Formato: AAAA-MM-DD.",
                    required = true,
                    example = "2026-11-12"
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate){

        return statementService.getStatementByPeriod(
                accountId,
                startDate,
                endDate
        );
    }
}
