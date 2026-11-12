package investment_account_api.transaction.scheduler;

import investment_account_api.transaction.service.TransactionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TransactionSettlementScheduler {

    private final TransactionService transactionService;

    public TransactionSettlementScheduler(TransactionService transactionService){
        this.transactionService = transactionService;
    }

    @Scheduled(fixedDelay = 30000)
    public void processPendingSettlements(){
        transactionService.processPendingSettlements();
    }
}
