package bw.ac.kgetsibank.dao;

import bw.ac.kgetsibank.model.BankTransaction;
import bw.ac.kgetsibank.model.TransactionType;
import bw.ac.kgetsibank.util.Database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    /**
     * Creates a transaction using an existing database connection.
     *
     * The connection is supplied by the service layer so that
     * the account balance update and transaction insertion can
     * occur in the same JDBC transaction.
     */
    public void create(
            Connection connection,
            long accountId,
            TransactionType type,
            BigDecimal amount,
            String description,
            BigDecimal balanceAfter
    ) throws SQLException {

        if (connection == null) {
            throw new IllegalArgumentException(
                    "Database connection is required."
            );
        }

        if (accountId <= 0) {
            throw new IllegalArgumentException(
                    "A valid account ID is required."
            );
        }

        if (type == null) {
            throw new IllegalArgumentException(
                    "Transaction type is required."
            );
        }

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Transaction amount cannot be negative."
            );
        }

        if (balanceAfter == null) {
            throw new IllegalArgumentException(
                    "Resulting account balance is required."
            );
        }

        String sql = """
                INSERT INTO transactions (
                    account_id,
                    transaction_type,
                    amount,
                    transaction_time,
                    description,
                    balance_after
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(
                    1,
                    accountId
            );

            statement.setString(
                    2,
                    type.name()
            );

            statement.setBigDecimal(
                    3,
                    amount
            );

            statement.setString(
                    4,
                    LocalDateTime.now().toString()
            );

            statement.setString(
                    5,
                    description
            );

            statement.setBigDecimal(
                    6,
                    balanceAfter
            );

            statement.executeUpdate();
        }
    }

    /**
     * Returns all transactions belonging to one account.
     */
    public List<BankTransaction> findByAccount(
            long accountId
    ) throws SQLException {

        if (accountId <= 0) {
            throw new IllegalArgumentException(
                    "A valid account ID is required."
            );
        }

        List<BankTransaction> transactions =
                new ArrayList<>();

        String sql = """
                SELECT
                    id,
                    account_id,
                    transaction_type,
                    amount,
                    transaction_time,
                    description,
                    balance_after
                FROM transactions
                WHERE account_id = ?
                ORDER BY transaction_time DESC, id DESC
                """;

        try (
                Connection connection =
                        Database.connect();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(
                    1,
                    accountId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                while (resultSet.next()) {

                    transactions.add(
                            mapTransaction(
                                    resultSet
                            )
                    );
                }
            }
        }

        return transactions;
    }

    /**
     * Returns one transaction by its database ID.
     */
    public BankTransaction findById(
            long transactionId
    ) throws SQLException {

        if (transactionId <= 0) {
            throw new IllegalArgumentException(
                    "A valid transaction ID is required."
            );
        }

        String sql = """
                SELECT
                    id,
                    account_id,
                    transaction_type,
                    amount,
                    transaction_time,
                    description,
                    balance_after
                FROM transactions
                WHERE id = ?
                """;

        try (
                Connection connection =
                        Database.connect();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(
                    1,
                    transactionId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    return mapTransaction(
                            resultSet
                    );
                }
            }
        }

        return null;
    }

    /**
     * Converts one JDBC ResultSet row into a BankTransaction.
     */
    private BankTransaction mapTransaction(
            ResultSet resultSet
    ) throws SQLException {

        String transactionTimeText =
                resultSet.getString(
                        "transaction_time"
                );

        /*
         * SQLite's datetime() may store values as:
         *
         * 2026-11-19 17:05:00
         *
         * while LocalDateTime.parse expects:
         *
         * 2026-11-19T17:05:00
         */
        if (transactionTimeText != null) {

            transactionTimeText =
                    transactionTimeText.replace(
                            ' ',
                            'T'
                    );
        }

        LocalDateTime transactionTime;

        if (transactionTimeText == null ||
                transactionTimeText.isBlank()) {

            transactionTime =
                    LocalDateTime.now();

        } else {

            transactionTime =
                    LocalDateTime.parse(
                            transactionTimeText
                    );
        }

        return new BankTransaction(
                resultSet.getLong(
                        "id"
                ),

                resultSet.getLong(
                        "account_id"
                ),

                TransactionType.valueOf(
                        resultSet.getString(
                                "transaction_type"
                        )
                ),

                resultSet.getBigDecimal(
                        "amount"
                ),

                transactionTime,

                resultSet.getString(
                        "description"
                ),

                resultSet.getBigDecimal(
                        "balance_after"
                )
        );
    }
}