package bw.ac.kgetsibank.controller;

import bw.ac.kgetsibank.dao.CustomerDAO;
import bw.ac.kgetsibank.model.Customer;

import java.sql.SQLException;

import java.util.List;
import java.util.Optional;

public class CustomerController {

    private final CustomerDAO customerDAO =
            new CustomerDAO();

    public Customer registerCustomer(
            String customerNumber,
            String firstName,
            String surname,
            String nationalId,
            String address,
            String phone,
            String email,
            boolean employed,
            String employerName,
            String employerAddress
    ) throws SQLException {

        require(
                customerNumber,
                "Customer number"
        );

        require(
                firstName,
                "First name"
        );

        require(
                surname,
                "Surname"
        );

        require(
                nationalId,
                "National ID"
        );

        if (employed) {

            require(
                    employerName,
                    "Employer name"
            );

            require(
                    employerAddress,
                    "Employer address"
            );
        }

        Customer customer =
                new Customer(
                        customerNumber.trim(),
                        firstName.trim(),
                        surname.trim(),
                        nationalId.trim(),
                        clean(address),
                        clean(phone),
                        clean(email),
                        employed,
                        nullable(
                                employerName
                        ),
                        nullable(
                                employerAddress
                        )
                );

        return customerDAO.create(
                customer
        );
    }

    public List<Customer> getAllCustomers()
            throws SQLException {

        return customerDAO.findAll();
    }

    public Optional<Customer> findCustomer(
            long customerId
    ) throws SQLException {

        return customerDAO.findById(
                customerId
        );
    }

    public void updateCustomer(
            Customer customer
    ) throws SQLException {

        if (customer == null ||
                customer.getId() <= 0) {

            throw new IllegalArgumentException(
                    "A saved customer is required."
            );
        }

        customerDAO.update(customer);
    }

    public void deactivateCustomer(
            long customerId
    ) throws SQLException {

        if (customerId <= 0) {

            throw new IllegalArgumentException(
                    "Invalid customer ID."
            );
        }

        customerDAO.deactivate(
                customerId
        );
    }

    private void require(
            String value,
            String field
    ) {

        if (value == null ||
                value.isBlank()) {

            throw new IllegalArgumentException(
                    field + " is required."
            );
        }
    }

    private String clean(
            String value
    ) {

        return value == null
                ? ""
                : value.trim();
    }

    private String nullable(
            String value
    ) {

        if (value == null ||
                value.isBlank()) {

            return null;
        }

        return value.trim();
    }
}