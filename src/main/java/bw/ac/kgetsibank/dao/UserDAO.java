package bw.ac.kgetsibank.dao;

import bw.ac.kgetsibank.model.User;
import bw.ac.kgetsibank.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Optional;

public class UserDAO {

    // =========================================================
    // FIND USER BY USERNAME
    // =========================================================

    public Optional<User> findByUsername(
            String username
    ) throws SQLException {

        if (username == null ||
                username.isBlank()) {

            return Optional.empty();
        }

        String sql = """
                SELECT *
                FROM users
                WHERE username = ?
                """;

        try (
                Connection connection =
                        Database.connect();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    username.trim()
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    return Optional.of(
                            mapUser(resultSet)
                    );
                }
            }
        }

        return Optional.empty();
    }

    // =========================================================
    // DEMONSTRATION AUTHENTICATION
    //
    // NOTE:
    // This matches our academic seed.sql file:
    //
    // officer   / officer123
    // customer1 / customer123
    //
    // Real banking software must NOT compare plaintext
    // passwords this way.
    // =========================================================

    public Optional<User> authenticateDemo(
            String username,
            String password
    ) throws SQLException {

        if (username == null ||
                username.isBlank() ||
                password == null ||
                password.isBlank()) {

            return Optional.empty();
        }

        String sql = """
                SELECT *
                FROM users
                WHERE username = ?
                  AND password_hash = ?
                  AND active = 1
                """;

        try (
                Connection connection =
                        Database.connect();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    username.trim()
            );

            statement.setString(
                    2,
                    password
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    return Optional.of(
                            mapUser(resultSet)
                    );
                }
            }
        }

        return Optional.empty();
    }

    // =========================================================
    // CREATE USER
    // =========================================================

    public User create(
            User user
    ) throws SQLException {

        if (user == null) {

            throw new IllegalArgumentException(
                    "User is required."
            );
        }

        String sql = """
                INSERT INTO users (
                    username,
                    password_hash,
                    role,
                    customer_id,
                    active
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                Connection connection =
                        Database.connect();

                PreparedStatement statement =
                        connection.prepareStatement(
                                sql,
                                PreparedStatement
                                        .RETURN_GENERATED_KEYS
                        )
        ) {

            statement.setString(
                    1,
                    user.getUsername()
            );

            statement.setString(
                    2,
                    user.getPasswordHash()
            );

            statement.setString(
                    3,
                    user.getRole()
            );

            if (user.getCustomerId() == null) {

                statement.setNull(
                        4,
                        java.sql.Types.INTEGER
                );

            } else {

                statement.setLong(
                        4,
                        user.getCustomerId()
                );
            }

            statement.setBoolean(
                    5,
                    user.isActive()
            );

            statement.executeUpdate();

            try (
                    ResultSet generatedKeys =
                            statement.getGeneratedKeys()
            ) {

                if (generatedKeys.next()) {

                    user.setId(
                            generatedKeys.getLong(1)
                    );
                }
            }
        }

        return user;
    }

    // =========================================================
    // CHANGE ACTIVE STATUS
    // =========================================================

    public void setActive(
            long userId,
            boolean active
    ) throws SQLException {

        String sql = """
                UPDATE users
                SET active = ?
                WHERE id = ?
                """;

        try (
                Connection connection =
                        Database.connect();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setBoolean(
                    1,
                    active
            );

            statement.setLong(
                    2,
                    userId
            );

            int rows =
                    statement.executeUpdate();

            if (rows == 0) {

                throw new SQLException(
                        "User could not be updated."
                );
            }
        }
    }

    // =========================================================
    // RESULTSET -> USER
    // =========================================================

    private User mapUser(
            ResultSet resultSet
    ) throws SQLException {

        Long customerId = null;

        Object customerValue =
                resultSet.getObject(
                        "customer_id"
                );

        if (customerValue != null) {

            customerId =
                    resultSet.getLong(
                            "customer_id"
                    );
        }

        User user =
                new User(

                        resultSet.getString(
                                "username"
                        ),

                        resultSet.getString(
                                "password_hash"
                        ),

                        resultSet.getString(
                                "role"
                        ),

                        customerId
                );

        user.setId(
                resultSet.getLong(
                        "id"
                )
        );

        user.setActive(
                resultSet.getBoolean(
                        "active"
                )
        );

        return user;
    }
}