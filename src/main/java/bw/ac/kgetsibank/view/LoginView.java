package bw.ac.kgetsibank.view;

import bw.ac.kgetsibank.controller.LoginController;
import bw.ac.kgetsibank.model.User;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Parent;

import javafx.scene.control.*;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class LoginView {

    private final BorderPane root =
            new BorderPane();

    private final LoginController controller;

    private final Consumer<User>
            successfulLogin;

    private final TextField username =
            new TextField(
                    "officer"
            );

    private final PasswordField password =
            new PasswordField();

    private final Label message =
            new Label();

    public LoginView(
            LoginController controller,
            Consumer<User> successfulLogin
    ) {

        this.controller =
                controller;

        this.successfulLogin =
                successfulLogin;

        password.setText(
                "officer123"
        );

        build();
    }

    private void build() {

        root.getStyleClass()
                .add(
                        "login-background"
                );

        Label bank =
                new Label(
                        "KGETSIBANK"
                );

        bank.getStyleClass()
                .add(
                        "bank-title"
                );

        Label subtitle =
                new Label(
                        "Banking Management System"
                );

        subtitle.getStyleClass()
                .add(
                        "subtitle"
                );

        username.setPromptText(
                "Username"
        );

        password.setPromptText(
                "Password"
        );

        Button login =
                new Button(
                        "Sign In"
                );

        login.getStyleClass()
                .add(
                        "primary-button"
                );

        login.setDefaultButton(
                true
        );

        login.setMaxWidth(
                Double.MAX_VALUE
        );

        login.setOnAction(
                event -> login()
        );

        message.getStyleClass()
                .add(
                        "error-label"
                );

        VBox card =
                new VBox(
                        14,
                        bank,
                        subtitle,
                        new Label(
                                "Username"
                        ),
                        username,
                        new Label(
                                "Password"
                        ),
                        password,
                        login,
                        message
                );

        card.setAlignment(
                Pos.CENTER_LEFT
        );

        card.setPadding(
                new Insets(35)
        );

        card.setMaxWidth(
                400
        );

        card.getStyleClass()
                .add(
                        "login-card"
                );

        root.setCenter(card);
    }

    private void login() {

        try {

            User user =
                    controller.login(
                            username.getText(),
                            password.getText()
                    );

            successfulLogin.accept(
                    user
            );

        } catch (Exception exception) {

            message.setText(
                    exception.getMessage()
            );
        }
    }

    public Parent getRoot() {

        return root;
    }
}