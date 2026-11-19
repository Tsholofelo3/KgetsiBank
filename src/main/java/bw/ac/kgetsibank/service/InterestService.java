package bw.ac.kgetsibank.service;

import bw.ac.kgetsibank.dao.AccountDAO;
import bw.ac.kgetsibank.dao.TransactionDAO;

import bw.ac.kgetsibank.model.*;

import bw.ac.kgetsibank.util.Database;

import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.List;

public class InterestService {

    private final AccountDAO accountDAO =
            new AccountDAO();

    private final TransactionDAO transactionDAO =
            new TransactionDAO();

    public int processMonthlyInterest()
            throws SQLException {

        List<Account> accounts =
                accountDAO
                        .findInterestBearingAccounts();

        int successfulAccounts = 0;

        for (Account account : accounts) {

            if (!(account instanceof
                    InterestBearing interestBearing)) {

                continue;
            }

            BigDecimal interest =
                    interestBearing
                            .calculateInterest();

            if (interest.compareTo(
                    BigDecimal.ZERO
            ) <= 0) {

                continue;
            }

            BigDecimal originalBalance =
                    account.getBalance();

            interestBearing
                    .applyMonthlyInterest();

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
                            TransactionType.INTEREST,
                            interest,
                            "Monthly interest",
                            account.getBalance()
                    );

                    connection.commit();

                    successfulAccounts++;

                } catch (Exception exception) {

                    connection.rollback();

                    account.setBalance(
                            originalBalance
                    );

                    System.err.println(
                            "Interest processing failed for "
                                    + account.getAccountNumber()
                                    + ": "
                                    + exception.getMessage()
                    );

                } finally {

                    connection.setAutoCommit(true);
                }
            }
        }

        return successfulAccounts;
    }
}