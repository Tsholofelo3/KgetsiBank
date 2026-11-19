package bw.ac.kgetsibank.controller;

import bw.ac.kgetsibank.dao.TransactionDAO;

import bw.ac.kgetsibank.model.Account;
import bw.ac.kgetsibank.model.BankTransaction;

import bw.ac.kgetsibank.service.BankingService;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.sql.SQLException;

import java.util.List;

public class TransactionController {

    private final BankingService bankingService =
            new BankingService();

    private final TransactionDAO transactionDAO =
            new TransactionDAO();

    public Account deposit(
            String accountNumber,
            String amount,
            String description
    ) throws SQLException {

        return bankingService.deposit(
                accountNumber,
                parsePositiveMoney(amount),
                description
        );
    }

    public Account withdraw(
            String accountNumber,
            String amount,
            String description
    ) throws SQLException {

        return bankingService.withdraw(
                accountNumber,
                parsePositiveMoney(amount),
                description
        );
    }

    public List<BankTransaction> getTransactionHistory(
            long accountId
    ) throws SQLException {

        if (accountId <= 0) {

            throw new IllegalArgumentException(
                    "Invalid account ID."
            );
        }

        return transactionDAO.findByAccount(
                accountId
        );
    }

    private BigDecimal parsePositiveMoney(
            String amount
    ) {

        if (amount == null ||
                amount.isBlank()) {

            throw new IllegalArgumentException(
                    "Transaction amount is required."
            );
        }

        try {

            BigDecimal value =
                    new BigDecimal(
                            amount.trim()
                    ).setScale(
                            2,
                            RoundingMode.HALF_UP
                    );

            if (value.compareTo(
                    BigDecimal.ZERO
            ) <= 0) {

                throw new IllegalArgumentException(
                        "Transaction amount must be greater than zero."
                );
            }

            return value;

        } catch (NumberFormatException exception) {

            throw new IllegalArgumentException(
                    "Enter a valid monetary amount."
            );
        }
    }
}