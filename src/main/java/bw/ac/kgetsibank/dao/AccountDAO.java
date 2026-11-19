package bw.ac.kgetsibank.dao;

import bw.ac.kgetsibank.model.Account;
import bw.ac.kgetsibank.model.AccountStatus;
import bw.ac.kgetsibank.model.AccountType;
import bw.ac.kgetsibank.model.ChequeAccount;
import bw.ac.kgetsibank.model.InvestmentAccount;
import bw.ac.kgetsibank.model.SavingsAccount;
import bw.ac.kgetsibank.util.Database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountDAO {

    // =========================================================
    // CREATE
    // =========================================================

    public Account create(
            Account account
    ) throws SQLException {

        String sql = """
                INSERT INTO accounts (
                    customer_id,
                    account_number,
                    account_type,
                    balance,
                    branch,
                    status,
                    date_opened
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection =
                        Database.connect();

                PreparedStatement statement =
                        connection.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            statement.setLong(
                    1,
                    account.getCustomerId()
            );

            statement.setString(
                    2,
                    account.getAccountNumber()
            );

            statement.setString(
                    3,
                    account.getAccountType().name()
            );

            statement.setBigDecimal(
                    4,
                    account.getBalance()
            );

            statement.setString(
                    5,
                    account.getBranch()
            );

            statement.setString(
                    6,
                    account.getStatus().name()
            );

            statement.setString(
                    7,
                    account.getDateOpened().toString()
            );

            statement.executeUpdate();

            try (
                    ResultSet generatedKeys =
                            statement.getGeneratedKeys()
            ) {

                if (generatedKeys.next()) {

                    account.setId(
                            generatedKeys.getLong(1)
                    );
                }
            }
        }

        return account;
    }

    // =========================================================
    // FIND BY DATABASE ID
    // =========================================================

    public Optional<Account> findById(
            long accountId
    ) throws SQLException {

        String sql = """
                SELECT
                    a.*,
                    c.employed,
                    c.employer_name,
                    c.employer_address
                FROM accounts a
                INNER JOIN customers c
                    ON c.id = a.customer_id
                WHERE a.id = ?
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

                if (resultSet.next()) {

                    return Optional.of(
                            mapAccount(resultSet)
                    );
                }
            }
        }

        return Optional.empty();
    }

    // =========================================================
    // FIND BY ACCOUNT NUMBER
    // =========================================================

    public Optional<Account> findByNumber(
            String accountNumber
    ) throws SQLException {

        if (accountNumber == null ||
                accountNumber.isBlank()) {

            return Optional.empty();
        }

        String sql = """
                SELECT
                    a.*,
                    c.employed,
                    c.employer_name,
                    c.employer_address
                FROM accounts a
                INNER JOIN customers c
                    ON c.id = a.customer_id
                WHERE a.account_number = ?
                """;

        try (
                Connection connection =
                        Database.connect();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    accountNumber.trim()
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    return Optional.of(
                            mapAccount(resultSet)
                    );
                }
            }
        }

        return Optional.empty();
    }

    // =========================================================
    // FIND CUSTOMER ACCOUNTS
    // =========================================================

    public List<Account> findByCustomer(
            long customerId
    ) throws SQLException {

        List<Account> accounts =
                new ArrayList<>();

        String sql = """
                SELECT
                    a.*,
                    c.employed,
                    c.employer_name,
                    c.employer_address
                FROM accounts a
                INNER JOIN customers c
                    ON c.id = a.customer_id
                WHERE a.customer_id = ?
                ORDER BY a.id
                """;

        try (
                Connection connection =
                        Database.connect();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(
                    1,
                    customerId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                while (resultSet.next()) {

                    accounts.add(
                            mapAccount(resultSet)
                    );
                }
            }
        }

        return accounts;
    }

    // =========================================================
    // FIND ALL ACCOUNTS
    // =========================================================

    public List<Account> findAll()
            throws SQLException {

        List<Account> accounts =
                new ArrayList<>();

        String sql = """
                SELECT
                    a.*,
                    c.employed,
                    c.employer_name,
                    c.employer_address
                FROM accounts a
                INNER JOIN customers c
                    ON c.id = a.customer_id
                ORDER BY a.id
                """;

        try (
                Connection connection =
                        Database.connect();

                Statement statement =
                        connection.createStatement();

                ResultSet resultSet =
                        statement.executeQuery(sql)
        ) {

            while (resultSet.next()) {

                accounts.add(
                        mapAccount(resultSet)
                );
            }
        }

        return accounts;
    }

    // =========================================================
    // FIND ACCOUNTS ELIGIBLE FOR INTEREST
    // =========================================================

    public List<Account>
    findInterestBearingAccounts()
            throws SQLException {

        List<Account> accounts =
                new ArrayList<>();

        String sql = """
                SELECT
                    a.*,
                    c.employed,
                    c.employer_name,
                    c.employer_address
                FROM accounts a
                INNER JOIN customers c
                    ON c.id = a.customer_id
                WHERE
                    a.account_type IN (
                        'SAVINGS',
                        'INVESTMENT'
                    )
                    AND a.status = 'ACTIVE'
                    AND a.balance > 0
                ORDER BY a.id
                """;

        try (
                Connection connection =
                        Database.connect();

                Statement statement =
                        connection.createStatement();

                ResultSet resultSet =
                        statement.executeQuery(sql)
        ) {

            while (resultSet.next()) {

                accounts.add(
                        mapAccount(resultSet)
                );
            }
        }

        return accounts;
    }

    // =========================================================
    // UPDATE BALANCE
    // =========================================================

    public void updateBalance(
            Connection connection,
            long accountId,
            BigDecimal balance
    ) throws SQLException {

        if (connection == null) {

            throw new IllegalArgumentException(
                    "Database connection is required."
            );
        }

        if (balance == null) {

            throw new IllegalArgumentException(
                    "Balance is required."
            );
        }

        String sql = """
                UPDATE accounts
                SET balance = ?
                WHERE id = ?
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setBigDecimal(
                    1,
                    balance
            );

            statement.setLong(
                    2,
                    accountId
            );

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows == 0) {

                throw new SQLException(
                        "Account balance could not be updated."
                );
            }
        }
    }

    // =========================================================
    // UPDATE STATUS
    // =========================================================

    public void updateStatus(
            long accountId,
            AccountStatus status
    ) throws SQLException {

        if (status == null) {

            throw new IllegalArgumentException(
                    "Account status is required."
            );
        }

        String sql = """
                UPDATE accounts
                SET status = ?
                WHERE id = ?
                """;

        try (
                Connection connection =
                        Database.connect();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    status.name()
            );

            statement.setLong(
                    2,
                    accountId
            );

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows == 0) {

                throw new SQLException(
                        "Account status could not be updated."
                );
            }
        }
    }

    // =========================================================
    // MAP DATABASE ROW TO DOMAIN SUBCLASS
    // =========================================================

    private Account mapAccount(
            ResultSet resultSet
    ) throws SQLException {

        long customerId =
                resultSet.getLong(
                        "customer_id"
                );

        String accountNumber =
                resultSet.getString(
                        "account_number"
                );

        BigDecimal balance =
                resultSet.getBigDecimal(
                        "balance"
                );

        String branch =
                resultSet.getString(
                        "branch"
                );

        AccountType accountType;

        try {

            accountType =
                    AccountType.valueOf(
                            resultSet.getString(
                                    "account_type"
                            )
                    );

        } catch (IllegalArgumentException exception) {

            throw new SQLException(
                    "Invalid account type stored in database.",
                    exception
            );
        }

        Account account;

        switch (accountType) {

            case SAVINGS ->

                    account =
                            new SavingsAccount(
                                    customerId,
                                    accountNumber,
                                    balance,
                                    branch
                            );

            case INVESTMENT ->

                    account =
                            new InvestmentAccount(
                                    customerId,
                                    accountNumber,
                                    balance,
                                    branch
                            );

            case CHEQUE ->

                    account =
                            new ChequeAccount(
                                    customerId,
                                    accountNumber,
                                    balance,
                                    branch,

                                    resultSet.getBoolean(
                                            "employed"
                                    ),

                                    resultSet.getString(
                                            "employer_name"
                                    ),

                                    resultSet.getString(
                                            "employer_address"
                                    )
                            );

            default ->

                    throw new SQLException(
                            "Unsupported account type."
                    );
        }

        account.setId(
                resultSet.getLong(
                        "id"
                )
        );

        try {

            account.setStatus(
                    AccountStatus.valueOf(
                            resultSet.getString(
                                    "status"
                            )
                    )
            );

        } catch (IllegalArgumentException exception) {

            throw new SQLException(
                    "Invalid account status stored in database.",
                    exception
            );
        }

        String dateOpened =
                resultSet.getString(
                        "date_opened"
                );

        if (dateOpened != null &&
                !dateOpened.isBlank()) {

            account.setDateOpened(
                    LocalDate.parse(
                            dateOpened
                    )
            );
        }

        return account;
    }
}