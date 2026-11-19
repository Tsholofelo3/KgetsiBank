package bw.ac.kgetsibank.controller;

import bw.ac.kgetsibank.model.User;
import bw.ac.kgetsibank.service.AuthenticationService;

import java.sql.SQLException;
import java.util.Optional;

public class LoginController {

    private final AuthenticationService
            authenticationService =
            new AuthenticationService();

    private User currentUser;

    public User login(
            String username,
            String password
    ) throws SQLException {

        Optional<User> result =
                authenticationService.login(
                        username,
                        password
                );

        if (result.isEmpty()) {

            throw new IllegalArgumentException(
                    "Invalid username or password."
            );
        }

        currentUser = result.get();

        return currentUser;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}