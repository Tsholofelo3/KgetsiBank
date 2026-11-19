package bw.ac.kgetsibank.model;

import java.math.BigDecimal;

public class SavingsAccount
        extends Account
        implements InterestBearing {

    // 0.05% per month = 0.0005
    public static final BigDecimal MONTHLY_RATE =
            new BigDecimal("0.0005");

    public SavingsAccount(
            long customerId,
            String accountNumber,
            BigDecimal openingBalance,
            String branch
    ) {

        super(
                customerId,
                accountNumber,
                openingBalance,
                branch
        );
    }

    @Override
    public void withdraw(BigDecimal amount) {

        throw new UnsupportedOperationException(
                "Savings accounts do not permit withdrawals."
        );
    }

    @Override
    public BigDecimal calculateInterest() {

        return money(
                getBalance().multiply(MONTHLY_RATE)
        );
    }

    @Override
    public void applyMonthlyInterest() {

        if (getStatus() == AccountStatus.ACTIVE &&
                getBalance().compareTo(BigDecimal.ZERO) > 0) {

            BigDecimal interest =
                    calculateInterest();

            if (interest.compareTo(BigDecimal.ZERO) > 0) {

                deposit(
                        interest,
                        "Monthly savings interest"
                );
            }
        }
    }

    @Override
    public AccountType getAccountType() {

        return AccountType.SAVINGS;
    }
}