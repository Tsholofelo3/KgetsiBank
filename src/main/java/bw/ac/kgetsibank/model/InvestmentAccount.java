package bw.ac.kgetsibank.model;

import java.math.BigDecimal;

public class InvestmentAccount
        extends Account
        implements InterestBearing {

    public static final BigDecimal MINIMUM_OPENING_DEPOSIT =
            new BigDecimal("500.00");

    // 5%
    public static final BigDecimal MONTHLY_RATE =
            new BigDecimal("0.05");

    public InvestmentAccount(
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

        if (openingBalance.compareTo(
                MINIMUM_OPENING_DEPOSIT
        ) < 0) {

            throw new IllegalArgumentException(
                    "Investment account requires "
                            + "a minimum opening deposit "
                            + "of BWP 500.00."
            );
        }
    }

    @Override
    public void withdraw(
            BigDecimal amount
    ) {

        standardWithdraw(amount);
    }

    @Override
    public BigDecimal calculateInterest() {

        return money(
                getBalance()
                        .multiply(MONTHLY_RATE)
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
                        "Monthly investment interest"
                );
            }
        }
    }

    @Override
    public AccountType getAccountType() {

        return AccountType.INVESTMENT;
    }
}