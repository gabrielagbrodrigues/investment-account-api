package investment_account_api.account.controller;


import investment_account_api.account.service.InvestmentAccountService;
import investment_account_api.account.entity.InvestmentAccount;
import investment_account_api.account.dto.OpenInvestmentAccountRequest;
import investment_account_api.account.dto.AccountBalanceResponse;
import investment_account_api.account.dto.DepositRequest;
import investment_account_api.transaction.service.TransactionService;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.awt.*;
import java.math.BigDecimal;


@RestController
@RequestMapping("api/v1/investment-accounts")
@Tag(
        name = "Contas de Investimento",
        description = "OPerações de abertura, ativação, deposito e consulta de contas de investimento.")

public class InvestmentAccountController {
    private final InvestmentAccountService investmentAccountService;
    private final TransactionService transactionService;


    public InvestmentAccountController(InvestmentAccountService investmentAccountService, TransactionService transactionService) {
        this.investmentAccountService = investmentAccountService;
        this.transactionService = transactionService;
    }
        @PostMapping
        @Operation(
                summary = "Abrir conta de investimento",
                description = "Cria uma nova conta de investimento vinculada ao identificador do cliente."
        )
        @ApiResponse(responseCode = "201", description = "Conta criada com sucesso.")
        public ResponseEntity<InvestmentAccount> openAccount(
                @RequestBody OpenInvestmentAccountRequest request){

            InvestmentAccount account =
                    investmentAccountService.openAccount(request.customerId());

            return ResponseEntity.status(HttpStatus.CREATED).body(account);
        }

        @PatchMapping("/{accountId}/activate")
        @Operation(
                summary = "Ativar conta de investimento",
                description = "Ativa uma conta de investimento previamente cadastrada."
        )
        @ApiResponse(responseCode = "200", description = "Conta ativada com sucesso.")
        public ResponseEntity<InvestmentAccount>activateAccount(
                @Parameter(description = "Identificador da conta de investimento.")
                @PathVariable Long accountId){

        InvestmentAccount account =
                investmentAccountService.activateAccount(accountId);

        return ResponseEntity.status(HttpStatus.OK).body(account);
        }
    @PostMapping("/{accountId}/deposit")
    @Operation(
            summary = "Realizar depósito",
            description = "Registra um deposito na conta de investimento informada."
    )
    @ApiResponse(responseCode = "200", description = "Deposito realizado com sucesso.")
    public ResponseEntity<InvestmentAccount> deposit(
            @Parameter(description = "Identificador da conta que receberá o depósito.")
            @PathVariable Long accountId,
            @RequestBody DepositRequest request){

        InvestmentAccount account =
                investmentAccountService.deposit(accountId, request.amount());
        return ResponseEntity.ok(account);
    }
    @GetMapping("/{accountId}")
    @Operation(
            summary = "Consultar conta",
            description = "Consulta os dados de uma conta de investimento pelo identificador."
    )
    @ApiResponse(responseCode = "200", description = "Dados da conta retornados com sucesso")
    public ResponseEntity<InvestmentAccount> findAccountById(
            @Parameter(description = "Identificador da conta de investimento.")
            @PathVariable Long accountId){

        InvestmentAccount account =
                investmentAccountService.findAccountById(accountId);

        return ResponseEntity.ok(account);
    }
    @GetMapping("/{accountId}/balance")
    @Operation(
            summary = "Consultar saldo e patrimônio",
            description = "Retorna as informações de saldo e patrimônio da conta de investimento."
    )
    @ApiResponse(responseCode = "200", description = "Informações financeiras retornadas com sucesso.")
    public ResponseEntity<AccountBalanceResponse> getAccountBalance(
            @Parameter(description = "Identificador da conta de investimento.")
            @PathVariable Long accountId) {

        AccountBalanceResponse balance =
                transactionService.getAccountBalance(accountId);
        return ResponseEntity.ok(balance);
    }

}
