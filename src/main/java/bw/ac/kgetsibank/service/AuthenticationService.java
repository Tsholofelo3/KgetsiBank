package bw.ac.kgetsibank.service;

import bw.ac.kgetsibank.dao.UserDAO;
import bw.ac.kgetsibank.model.User;

import java.sql.SQLException;
import java.util.Optional;

public class AuthenticationService {

    private final UserDAO userDAO =
            new UserDAO();

    public Optional<User> login(
            String username,
            String password
    ) throws SQLException {

        if (username == null ||
                username.isBlank()) {

            throw new IllegalArgumentException(
                    "Username is required."
            );
        }

        if (password == null ||
                password.isBlank()) {

            throw new IllegalArgumentException(
                    "Password is required."
            );
        }

        return userDAO.authenticateDemo(
                username.trim(),
                password
        );
    }

    public boolean isOfficer(
            User user
    ) {

        return user != null
                && user.isActive()
                && (
                "OFFICER".equals(
                        user.getRole()
                )
                        ||
                        "ADMIN".equals(
                                user.getRole()
                        )
        );
    }

    public boolean isCustomer(
            User user
    ) {

        return user != null
                && user.isActive()
                && "CUSTOMER".equals(
                user.getRole()
        );
    }
}