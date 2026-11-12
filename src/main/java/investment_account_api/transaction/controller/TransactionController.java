package investment_account_api.transaction.controller;

import investment_account_api.transaction.dto.CreateTransactionRequest;
import investment_account_api.transaction.dto.TransactionResponse;
import investment_account_api.transaction.entity.Transaction;
import investment_account_api.transaction.service.TransactionService;
import investment_account_api.transaction.dto.TransactionCreationResult;
import investment_account_api.transaction.dto.TransactionCreationResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/api/v1/investment-accounts/{accountId}/transactions")
@Tag(
        name = "Movimentações Financeiras",
        description = "Registro e gerenciamento de operações financeiras nas contas de investimento."
)
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    }

    @PostMapping
    @Operation(
            summary = "Registrar movimentaçõies financeira",
            description = "Registra uma operação de depósito, compra ou venda na conta de investimento." +
                    "A operaçõa possui controle de idempotência para evitar registros duplicados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Nova movimentação financeira criada com sucesso."
            ),
    })
    public ResponseEntity<TransactionCreationResponse> createTransaction(

            @Parameter(
                    description = "Identificador da conta de investimento.",
                    required = true
            )
            @PathVariable Long accountId,

            @RequestBody CreateTransactionRequest request) {

        TransactionCreationResult result = transactionService.createTransaction(
                accountId,
                request.type(),
                request.amount(),
                request.idempotencyKey(),
                request.transactionDate(),
                request.settlementDate()
        );


        Transaction transaction = result.transaction();

        TransactionResponse response = new TransactionResponse(
                transaction.getId(),
                transaction.getInvestmentAccount().getId(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getSettlementDate()
        );

        String message = result.reused()
                ? "Esta operação já foi realizada anteriormente. Retornando a transação existente."
                : "Transação criada com sucesso.";

        TransactionCreationResponse creationResponse =
                new TransactionCreationResponse(message, response);

        HttpStatus status = result.reused()
                ? HttpStatus.OK
                : HttpStatus.CREATED;

        return ResponseEntity.status(status).body(creationResponse);
    }

}
