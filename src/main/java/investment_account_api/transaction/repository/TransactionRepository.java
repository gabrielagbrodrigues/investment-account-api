package investment_account_api.transaction.repository;


import investment_account_api.transaction.entity.Transaction;
import investment_account_api.transaction.enums.TransactionStatus;
import investment_account_api.transaction.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;


import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

public interface TransactionRepository extends JpaRepository<Transaction, Long>{
    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);

    List<Transaction> findByInvestmentAccount_IdOrderByTransactionDateDescIdDesc(Long accountId);

    @Query("""
            SELECT t FROM Transaction t
            WHERE t.status = ?1
            AND t.settlementDate <= ?2
            """)
    List<Transaction> findPendingTransactionsToSettle(
            TransactionStatus status,
            LocalDate settlementDate);

    @Query("""
            SELECT COALESCE(SUM(t.amount),0)
            FROM Transaction t
            WHERE t.investmentAccount.id = :accountId
            AND t.status = :status
            AND t.type = :type
            """)
    BigDecimal sumTransactionsByAccountStatusAndType(
            @Param("accountId") Long accountId,
            @Param("status")TransactionStatus status,
            @Param("type")TransactionType type
    );

   @Query("""
           SELECT t FROM Transaction t
           WHERE t.investmentAccount.id = :accountId
           AND t.transactionDate BETWEEN :startDate AND :endDate
           ORDER BY t.transactionDate DESC, t.id DESC    
           """)
    List<Transaction> findTransactionsByAccountAndPeriod(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
   );

}
