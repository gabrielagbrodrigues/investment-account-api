package investment_account_api.transaction.service;

import investment_account_api.account.entity.InvestmentAccount;
import investment_account_api.account.enums.AccountStatus;
import investment_account_api.account.repository.InvestmentAccountRepository;
import investment_account_api.transaction.entity.Transaction;
import investment_account_api.transaction.enums.TransactionStatus;
import investment_account_api.transaction.enums.TransactionType;
import investment_account_api.transaction.repository.TransactionRepository;
import investment_account_api.account.dto.AccountBalanceResponse;
import investment_account_api.transaction.dto.TransactionCreationResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final InvestmentAccountRepository accountRepository;
    private final Clock clock;


    public TransactionService(
            TransactionRepository transactionRepository,
            InvestmentAccountRepository accountRepository, Clock clock) {

        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.clock = clock;
    }

    @Transactional
    public TransactionCreationResult createTransaction(
            Long accountId,
            TransactionType type,
            BigDecimal amount,
            String idempotencyKey,
            LocalDate transactionDate,
            LocalDate settlementDate
    ) {

        Optional<Transaction> existingTransaction =
                transactionRepository.findByIdempotencyKey(idempotencyKey);

        if (existingTransaction.isPresent()) {

            Transaction existing = existingTransaction.get();

            boolean sameRequest =
                    Objects.equals(existing.getInvestmentAccount().getId(), accountId)
                            && existing.getType() == type
                            && existing.getAmount().compareTo(amount) == 0
                            && existing.getTransactionDate().equals(transactionDate)
                            && existing.getSettlementDate().equals(settlementDate);

            if (sameRequest) {
                return new TransactionCreationResult(existing, true);
            }

            throw new IllegalArgumentException(
                    "Chave de idempotência já utilizada em outra operação."
            );
        }

        InvestmentAccount account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Conta não encontrada.")
                );
        BigDecimal pendingDebits =
                transactionRepository.sumTransactionsByAccountStatusAndType(
                        accountId,
                        TransactionStatus.PENDING,
                        TransactionType.PURCHASE
                );
        if (pendingDebits == null){
            pendingDebits = BigDecimal.ZERO;

            System.out.println("Saldo atual: " + account.getBalance());
            System.out.println("Valores reservados: " + pendingDebits);
            System.out.println("Valor solicitado: " + amount);
        }

        BigDecimal availableBalance = account.getBalance()
                .subtract(pendingDebits);

        if (availableBalance.compareTo(amount) < 0){
            throw new IllegalArgumentException(
                    "Saldo disponível insuficiente para realizar a transação"
            );
        }

        Transaction transaction = new Transaction(
                account,
                type,
                TransactionStatus.PENDING,
                amount,
                idempotencyKey,
                transactionDate,
                settlementDate
        );

        Transaction savedTransaction = transactionRepository.save(transaction);

        return new TransactionCreationResult(savedTransaction, false);
    }

    @Transactional
    public Transaction settleTransaction(
            Long accountId,
            Long transactionId) {

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Transação não encontrada."
                ));
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new IllegalStateException(
                    "A transação não está pendente."
            );
        }
        if (transaction.getSettlementDate().isAfter(LocalDate.now(clock))) {
            throw new IllegalStateException(
                    "A data de liquidação ainda não foi atingida."
            );
        }
        if (!Objects.equals(
                transaction.getInvestmentAccount().getId(),
                accountId)) {
            throw new IllegalArgumentException(
                    "A transação não pertence a esta conta.");
        }
        InvestmentAccount account = transaction.getInvestmentAccount();

        if (transaction.getType() == TransactionType.PURCHASE) {
            if (account.getBalance().compareTo(transaction.getAmount()) < 0) {

                transaction.cancel();

                transactionRepository.save(transaction);

                return transaction;
            }

            account.debit(transaction.getAmount());
        } else if (transaction.getType() == TransactionType.SALE) {
            account.credit(transaction.getAmount());
        }

        transaction.settle(LocalDate.now(clock));
        accountRepository.save(account);
        return transactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByAccount(Long accountId) {

        accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Conta de investimento não encontrada."
                ));

        return transactionRepository
                .findByInvestmentAccount_IdOrderByTransactionDateDescIdDesc(accountId);
    }

    @Transactional(readOnly = true)
    public AccountBalanceResponse getAccountBalance(Long accountId) {

        InvestmentAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Conta de investimento não encontrada."
                ));

        List<Transaction> transactions =
                transactionRepository.findByInvestmentAccount_IdOrderByTransactionDateDescIdDesc(accountId);

        BigDecimal pendingDebits = BigDecimal.ZERO;
        BigDecimal pendingCredits = BigDecimal.ZERO;
        BigDecimal totalPurchases = BigDecimal.ZERO;
        BigDecimal totalSales = BigDecimal.ZERO;
        BigDecimal lastDebitAmount = null;
        LocalDate lastDebitDate = null;
        BigDecimal lastCreditAmount = null;
        LocalDate lastCreditDate = null;
        LocalDate pendingDebitsSettlementeDate = null;
        LocalDate pendingCreditsSettlementeDate = null;
        LocalDate projectionLimiteDate = LocalDate.now(clock).plusDays(10);
        BigDecimal pendingDebitsD10 = BigDecimal.ZERO;
        BigDecimal pendingCreditsD10 = BigDecimal.ZERO;


        for (Transaction transaction : transactions) {

            if (transaction.getStatus() == TransactionStatus.CANCELLED) {
                continue;
            }

            if (transaction.getStatus() == TransactionStatus.SETTLED) {

                if (transaction.getType() == TransactionType.PURCHASE
                        && transaction.getSettledAt() != null
                        && (lastDebitDate == null ||
                        transaction.getSettledAt().isAfter(lastDebitDate))) {

                    lastDebitAmount = transaction.getAmount();
                    lastDebitDate = transaction.getSettledAt();
                }

                if (transaction.getType() == TransactionType.SALE
                        && transaction.getSettledAt() != null
                        && (lastCreditDate == null ||
                        transaction.getSettledAt().isAfter(lastCreditDate))) {

                    lastCreditAmount = transaction.getAmount();
                    lastCreditDate = transaction.getSettledAt();
                }
            }

            if (transaction.getType() == TransactionType.PURCHASE) {

                totalPurchases = totalPurchases.add(transaction.getAmount());

                if (transaction.getStatus() == TransactionStatus.PENDING) {

                    pendingDebits = pendingDebits.add(transaction.getAmount());

                    if (transaction.getSettlementDate() != null &&
                            !transaction.getSettlementDate().isAfter(projectionLimiteDate)) {

                        pendingDebitsD10 = pendingDebitsD10.add(transaction.getAmount());

                        if (pendingDebitsSettlementeDate == null ||
                                transaction.getSettlementDate().isBefore(pendingDebitsSettlementeDate)) {

                            pendingDebitsSettlementeDate = transaction.getSettlementDate();
                        }
                    }
                }

            } else if (transaction.getType() == TransactionType.SALE) {

                totalSales = totalSales.add(transaction.getAmount());

                if (transaction.getStatus() == TransactionStatus.PENDING) {

                    pendingCredits = pendingCredits.add(transaction.getAmount());

                    if (transaction.getSettlementDate() != null &&
                            !transaction.getSettlementDate().isAfter(projectionLimiteDate)) {

                        pendingCreditsD10 = pendingCreditsD10.add(transaction.getAmount());
                    }

                    if (transaction.getSettlementDate() != null &&
                            (pendingCreditsSettlementeDate == null ||
                                    transaction.getSettlementDate().isBefore(pendingCreditsSettlementeDate))) {

                        pendingCreditsSettlementeDate = transaction.getSettlementDate();
                    }
                }
            }
        }

        BigDecimal projectBalance = account.getBalance()
                    .subtract(pendingDebits)
                    .add(pendingCredits);

            BigDecimal projectedBalanceD10 = account.getBalance()
                    .subtract(pendingDebitsD10)
                    .add(pendingCreditsD10);

            return new AccountBalanceResponse(
                    account.getBalance(),
                    pendingDebits,
                    pendingDebitsSettlementeDate,
                    pendingCredits,
                    pendingCreditsSettlementeDate,
                    projectBalance,
                    projectedBalanceD10,
                    totalPurchases,
                    totalSales,
                    lastDebitAmount,
                    lastDebitDate,
                    lastCreditAmount,
                    lastCreditDate

            );
        }

        @Transactional
        public void processPendingSettlements () {

            List<Transaction> pendingTransactions =
                    transactionRepository
                            .findPendingTransactionsToSettle(
                                    TransactionStatus.PENDING,
                                    LocalDate.now(clock)
                            );

            for (Transaction transaction : pendingTransactions) {

                settleTransaction(
                        transaction.getInvestmentAccount().getId(),
                        transaction.getId()
                );
            }

            System.out.println(
                    "Data simulada atual: " + LocalDate.now(clock)
            );
        }


    }


