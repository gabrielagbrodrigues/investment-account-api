package investment_account_api.account.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import investment_account_api.account.enums.AccountStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;

import java.time.LocalDateTime;

@Entity
@Table(name = "investment_account")
public class InvestmentAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("identificador")
    private Long id;

    @Column(name = "account_number", nullable = false, unique = true, length = 20)
    @JsonProperty("numeroDaConta")
    private String accountNumber;

    @Column(name = "customer_id", nullable = false, length = 50)
    @JsonProperty("identificadorDoCliente")
    private String customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @JsonProperty("situacaoDaConta")
    private AccountStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonProperty("dataDeCriacao")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @JsonProperty("dataDeAtualizacao")
    private LocalDateTime updatedAt;

    @Column(nullable = false, precision = 19, scale = 2)
    @JsonProperty("saldoAtual")
    private BigDecimal balance;

    protected InvestmentAccount(){
    }

    public InvestmentAccount(String accountNumber,String customerId){
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.status = AccountStatus.PENDDING_ACTIVATION;
        this.balance = BigDecimal.ZERO;
    }

    public void activate(){
        if (this.status != AccountStatus.PENDDING_ACTIVATION){
            throw new IllegalStateException(
                    "A conta não está aguardando ativação."
            );
        }
        this.status = AccountStatus.ACTIVE;
    }

    public void debit(BigDecimal amount){
        validateAmount(amount);

        if (this.balance.compareTo(amount)<0){
            throw new IllegalStateException(
                    "Saldo insuficiente."
            );
        }
        this.balance = this.balance.subtract(amount);
    }

    public void credit(BigDecimal amount){
        validateAmount(amount);
        this.balance = this.balance.add(amount);
    }
    public void validateAmount(BigDecimal amount){
        if (amount == null || amount.compareTo(BigDecimal.ZERO)<= 0){
            throw new IllegalStateException(
                    "O valor deve ser maior que zero."
            );
        }
    }
    public BigDecimal getBalance(){
        return balance;
    }

    @PrePersist
    protected void onCreate(){
        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;
    }
    @PreUpdate
    protected void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
