package bw.ac.kgetsibank.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public abstract class Account {

    private long id;
    private final long customerId;
    private final String accountNumber;

    private BigDecimal balance;

    private final String branch;

    private AccountStatus status;
    private LocalDate dateOpened;

    protected Account(
            long customerId,
            String accountNumber,
            BigDecimal openingBalance,
            String branch
    ) {

        if (customerId <= 0) {
            throw new IllegalArgumentException(
                    "Customer ID must be greater than zero."
            );
        }

        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Account number is required."
            );
        }

        if (openingBalance == null ||
                openingBalance.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Opening balance cannot be negative."
            );
        }

        if (branch == null || branch.isBlank()) {
            throw new IllegalArgumentException(
                    "Branch is required."
            );
        }

        this.customerId = customerId;
        this.accountNumber = accountNumber.trim();
        this.balance = money(openingBalance);
        this.branch = branch.trim();

        this.status = AccountStatus.ACTIVE;
        this.dateOpened = LocalDate.now();
    }

    public void deposit(BigDecimal amount) {

        requireActive();
        requirePositive(amount);

        balance = money(
                balance.add(amount)
        );
    }

    // Method overloading
    public void deposit(
            BigDecimal amount,
            String description
    ) {
        deposit(amount);
    }

    public abstract void withdraw(
            BigDecimal amount
    );

    protected void standardWithdraw(
            BigDecimal amount
    ) {

        requireActive();
        requirePositive(amount);

        if (amount.compareTo(balance) > 0) {

            throw new IllegalArgumentException(
                    "Insufficient available balance."
            );
        }

        balance = money(
                balance.subtract(amount)
        );
    }

    protected void requireActive() {

        if (status != AccountStatus.ACTIVE) {

            throw new IllegalStateException(
                    "Account is not active."
            );
        }
    }

    protected static void requirePositive(
            BigDecimal amount
    ) {

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Amount must be greater than zero."
            );
        }
    }

    protected static BigDecimal money(
            BigDecimal amount
    ) {

        if (amount == null) {

            throw new IllegalArgumentException(
                    "Amount cannot be null."
            );
        }

        return amount.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }

    public abstract AccountType getAccountType();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCustomerId() {
        return customerId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(
            BigDecimal balance
    ) {

        if (balance == null ||
                balance.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Balance cannot be negative."
            );
        }

        this.balance = money(balance);
    }

    public String getBranch() {
        return branch;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(
            AccountStatus status
    ) {

        if (status == null) {

            throw new IllegalArgumentException(
                    "Account status is required."
            );
        }

        this.status = status;
    }

    public LocalDate getDateOpened() {
        return dateOpened;
    }

    public void setDateOpened(
            LocalDate dateOpened
    ) {

        if (dateOpened == null) {

            throw new IllegalArgumentException(
                    "Opening date is required."
            );
        }

        this.dateOpened = dateOpened;
    }

    @Override
    public String toString() {

        return accountNumber
                + " | "
                + getAccountType()
                + " | BWP "
                + balance;
    }
}