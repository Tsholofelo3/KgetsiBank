package bw.ac.kgetsibank.controller;

import bw.ac.kgetsibank.dao.AccountDAO;

import bw.ac.kgetsibank.model.*;

import bw.ac.kgetsibank.service.BankingService;
import bw.ac.kgetsibank.service.InterestService;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.sql.SQLException;

import java.util.List;

public class AccountController {

    private final AccountDAO accountDAO =
            new AccountDAO();

    private final BankingService bankingService =
            new BankingService();

    private final InterestService interestService =
            new InterestService();

    public Account openAccount(
            Customer customer,
            AccountType type,
            String openingBalance,
            String branch
    ) throws SQLException {

        return bankingService.openAccount(
                customer,
                type,
                parseMoneyAllowZero(
                        openingBalance
                ),
                branch
        );
    }

    public Account findAccount(
            String accountNumber
    ) throws SQLException {

        return bankingService.getAccount(
                accountNumber
        );
    }

    public List<Account> getCustomerAccounts(
            long customerId
    ) throws SQLException {

        if (customerId <= 0) {

            throw new IllegalArgumentException(
                    "Invalid customer ID."
            );
        }

        return accountDAO.findByCustomer(
                customerId
        );
    }

    public int processMonthlyInterest()
            throws SQLException {

        return interestService
                .processMonthlyInterest();
    }

    private BigDecimal parseMoneyAllowZero(
            String value
    ) {

        if (value == null ||
                value.isBlank()) {

            throw new IllegalArgumentException(
                    "Amount is required."
            );
        }

        try {

            BigDecimal amount =
                    new BigDecimal(
                            value.trim()
                    ).setScale(
                            2,
                            RoundingMode.HALF_UP
                    );

            if (amount.compareTo(
                    BigDecimal.ZERO
            ) < 0) {

                throw new IllegalArgumentException(
                        "Amount cannot be negative."
                );
            }

            return amount;

        } catch (NumberFormatException exception) {

            throw new IllegalArgumentException(
                    "Enter a valid monetary amount."
            );
        }
    }
}