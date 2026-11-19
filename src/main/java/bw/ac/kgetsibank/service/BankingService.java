package bw.ac.kgetsibank.service;

import bw.ac.kgetsibank.dao.AccountDAO;
import bw.ac.kgetsibank.dao.TransactionDAO;

import bw.ac.kgetsibank.model.*;

import bw.ac.kgetsibank.util.Database;

import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.concurrent.atomic.AtomicLong;

public class BankingService {

    private static final AtomicLong NUMBER_SEQUENCE =
            new AtomicLong(
                    System.currentTimeMillis()
            );

    private final AccountDAO accountDAO =
            new AccountDAO();

    private final TransactionDAO transactionDAO =
            new TransactionDAO();

    public Account openAccount(
            Customer customer,
            AccountType accountType,
            BigDecimal openingBalance,
            String branch
    ) throws SQLException {

        if (customer == null) {

            throw new IllegalArgumentException(
                    "Customer is required."
            );
        }

        if (customer.getId() <= 0) {

            throw new IllegalArgumentException(
                    "Customer must be saved first."
            );
        }

        if (accountType == null) {

            throw new IllegalArgumentException(
                    "Account type is required."
            );
        }

        if (openingBalance == null ||
                openingBalance.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Opening balance cannot be negative."
            );
        }

        if (branch == null ||
                branch.isBlank()) {

            throw new IllegalArgumentException(
                    "Branch is required."
            );
        }

        String accountNumber =
                generateAccountNumber(
                        accountType
                );

        Account account =
                switch (accountType) {

                    case SAVINGS ->
                            new SavingsAccount(
                                    customer.getId(),
                                    accountNumber,
                                    openingBalance,
                                    branch
                            );

                    case INVESTMENT ->
                            new InvestmentAccount(
                                    customer.getId(),
                                    accountNumber,
                                    openingBalance,
                                    branch
                            );

                    case CHEQUE ->
                            new ChequeAccount(
                                    customer.getId(),
                                    accountNumber,
                                    openingBalance,
                                    branch,
                                    customer.isEmployed(),
                                    customer.getEmployerName(),
                                    customer.getEmployerAddress()
                            );
                };

        accountDAO.create(account);

        if (openingBalance.compareTo(BigDecimal.ZERO) > 0) {

            try (Connection connection =
                         Database.connect()) {

                transactionDAO.create(
                        connection,
                        account.getId(),
                        TransactionType.OPENING_DEPOSIT,
                        openingBalance,
                        "Opening deposit",
                        openingBalance
                );
            }
        }

        return account;
    }

    public Account deposit(
            String accountNumber,
            BigDecimal amount,
            String description
    ) throws SQLException {

        Account account =
                getAccount(
                        accountNumber
                );

        account.deposit(
                amount,
                description
        );

        saveMonetaryTransaction(
                account,
                TransactionType.DEPOSIT,
                amount,
                normalizeDescription(
                        description,
                        "Deposit"
                )
        );

        return account;
    }

    public Account withdraw(
            String accountNumber,
            BigDecimal amount,
            String description
    ) throws SQLException {

        Account account =
                getAccount(
                        accountNumber
                );

        account.withdraw(amount);

        saveMonetaryTransaction(
                account,
                TransactionType.WITHDRAWAL,
                amount,
                normalizeDescription(
                        description,
                        "Withdrawal"
                )
        );

        return account;
    }

    private void saveMonetaryTransaction(
            Account account,
            TransactionType type,
            BigDecimal amount,
            String description
    ) throws SQLException {

        try (Connection connection =
                     Database.connect()) {

            connection.setAutoCommit(false);

            try {

                accountDAO.updateBalance(
                        connection,
                        account.getId(),
                        account.getBalance()
                );

                transactionDAO.create(
                        connection,
                        account.getId(),
                        type,
                        amount,
                        description,
                        account.getBalance()
                );

                connection.commit();

            } catch (Exception exception) {

                connection.rollback();

                if (exception instanceof SQLException sqlException) {
                    throw sqlException;
                }

                throw new SQLException(
                        "Transaction could not be completed.",
                        exception
                );

            } finally {

                connection.setAutoCommit(true);
            }
        }
    }

    public Account getAccount(
            String accountNumber
    ) throws SQLException {

        if (accountNumber == null ||
                accountNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Account number is required."
            );
        }

        return accountDAO
                .findByNumber(
                        accountNumber.trim()
                )
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "Account not found."
                                )
                );
    }

    private String generateAccountNumber(
            AccountType accountType
    ) {

        String prefix =
                switch (accountType) {

                    case SAVINGS -> "SAV";
                    case INVESTMENT -> "INV";
                    case CHEQUE -> "CHQ";
                };

        return prefix
                + NUMBER_SEQUENCE.incrementAndGet();
    }

    private String normalizeDescription(
            String description,
            String defaultValue
    ) {

        if (description == null ||
                description.isBlank()) {

            return defaultValue;
        }

        return description.trim();
    }
}