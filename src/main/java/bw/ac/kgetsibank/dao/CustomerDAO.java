package bw.ac.kgetsibank.dao;

import bw.ac.kgetsibank.model.Customer;
import bw.ac.kgetsibank.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerDAO {

    public Customer create(
            Customer customer
    ) throws SQLException {

        String sql = """
                INSERT INTO customers (
                    customer_number,
                    first_name,
                    surname,
                    national_id,
                    address,
                    phone,
                    email,
                    employed,
                    employer_name,
                    employer_address,
                    active
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
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

            statement.setString(
                    1,
                    customer.getCustomerNumber()
            );

            statement.setString(
                    2,
                    customer.getFirstName()
            );

            statement.setString(
                    3,
                    customer.getSurname()
            );

            statement.setString(
                    4,
                    customer.getNationalId()
            );

            statement.setString(
                    5,
                    customer.getAddress()
            );

            statement.setString(
                    6,
                    customer.getPhone()
            );

            statement.setString(
                    7,
                    customer.getEmail()
            );

            statement.setBoolean(
                    8,
                    customer.isEmployed()
            );

            statement.setString(
                    9,
                    customer.getEmployerName()
            );

            statement.setString(
                    10,
                    customer.getEmployerAddress()
            );

            statement.setBoolean(
                    11,
                    customer.isActive()
            );

            statement.executeUpdate();

            try (
                    ResultSet generatedKeys =
                            statement.getGeneratedKeys()
            ) {

                if (generatedKeys.next()) {

                    customer.setId(
                            generatedKeys.getLong(1)
                    );
                }
            }
        }

        return customer;
    }

    public Optional<Customer> findById(
            long customerId
    ) throws SQLException {

        String sql = """
                SELECT *
                FROM customers
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
                    customerId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    return Optional.of(
                            mapCustomer(resultSet)
                    );
                }
            }
        }

        return Optional.empty();
    }

    public Optional<Customer> findByCustomerNumber(
            String customerNumber
    ) throws SQLException {

        String sql = """
                SELECT *
                FROM customers
                WHERE customer_number = ?
                """;

        try (
                Connection connection =
                        Database.connect();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    customerNumber
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    return Optional.of(
                            mapCustomer(resultSet)
                    );
                }
            }
        }

        return Optional.empty();
    }

    public List<Customer> findAll()
            throws SQLException {

        List<Customer> customers =
                new ArrayList<>();

        String sql = """
                SELECT *
                FROM customers
                WHERE active = 1
                ORDER BY surname, first_name
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

                customers.add(
                        mapCustomer(resultSet)
                );
            }
        }

        return customers;
    }

    public void update(
            Customer customer
    ) throws SQLException {

        String sql = """
                UPDATE customers
                SET
                    first_name = ?,
                    surname = ?,
                    national_id = ?,
                    address = ?,
                    phone = ?,
                    email = ?,
                    employed = ?,
                    employer_name = ?,
                    employer_address = ?,
                    active = ?
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
                    customer.getFirstName()
            );

            statement.setString(
                    2,
                    customer.getSurname()
            );

            statement.setString(
                    3,
                    customer.getNationalId()
            );

            statement.setString(
                    4,
                    customer.getAddress()
            );

            statement.setString(
                    5,
                    customer.getPhone()
            );

            statement.setString(
                    6,
                    customer.getEmail()
            );

            statement.setBoolean(
                    7,
                    customer.isEmployed()
            );

            statement.setString(
                    8,
                    customer.getEmployerName()
            );

            statement.setString(
                    9,
                    customer.getEmployerAddress()
            );

            statement.setBoolean(
                    10,
                    customer.isActive()
            );

            statement.setLong(
                    11,
                    customer.getId()
            );

            statement.executeUpdate();
        }
    }

    public void deactivate(
            long customerId
    ) throws SQLException {

        String sql = """
                UPDATE customers
                SET active = 0
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
                    customerId
            );

            statement.executeUpdate();
        }
    }

    private Customer mapCustomer(
            ResultSet resultSet
    ) throws SQLException {

        Customer customer =
                new Customer(
                        resultSet.getString(
                                "customer_number"
                        ),
                        resultSet.getString(
                                "first_name"
                        ),
                        resultSet.getString(
                                "surname"
                        ),
                        resultSet.getString(
                                "national_id"
                        ),
                        resultSet.getString(
                                "address"
                        ),
                        resultSet.getString(
                                "phone"
                        ),
                        resultSet.getString(
                                "email"
                        ),
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

        customer.setId(
                resultSet.getLong(
                        "id"
                )
        );

        customer.setActive(
                resultSet.getBoolean(
                        "active"
                )
        );

        return customer;
    }
}