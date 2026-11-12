package investment_account_api.transaction.entity;

import investment_account_api.account.entity.InvestmentAccount;
import investment_account_api.transaction.enums.TransactionStatus;
import investment_account_api.transaction.enums.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "investment_transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investment_account_id", nullable = false)
    private InvestmentAccount investmentAccount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, unique = true, length = 100)
    private String idempotencyKey;

    @Column(nullable = false)
    private LocalDate transactionDate;

    @Column(nullable = false)
    private LocalDate settlementDate;

    private LocalDate settledAt;

    protected Transaction(){}

    public Transaction(
            InvestmentAccount investmentAccount,
            TransactionType type,
            TransactionStatus status,
            BigDecimal amount,
            String idempotencyKey,
            LocalDate transactionDate,
            LocalDate settlementDate){

        this.investmentAccount = investmentAccount;
        this.type = type;
        this.status = status;
        this.amount = amount;
        this.idempotencyKey = idempotencyKey;
        this.transactionDate = transactionDate;
        this.settlementDate = settlementDate;
    }

    public Long getId() {
        return id;
    }

    public InvestmentAccount getInvestmentAccount() {
        return investmentAccount;
    }

    public TransactionType getType() {
        return type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public LocalDate getSettlementDate() {
        return settlementDate;
    }

    public LocalDate getSettledAt() {
        return settledAt;
    }

    public void settle(LocalDate settledAt){
        if (this.status != TransactionStatus.PENDING){
            throw new IllegalStateException(
                    "Somente transações pendentes podem ser liquidadas."
            );
        }
        this.status = TransactionStatus.SETTLED;
        this.settledAt = settledAt;
    }

    public void cancel(){
        if (this.status != TransactionStatus.PENDING){
            throw new IllegalStateException(
                    "Somente transações pendentes podem ser canceladas"
            );
        }
        this.status = TransactionStatus.CANCELLED;
    }
}