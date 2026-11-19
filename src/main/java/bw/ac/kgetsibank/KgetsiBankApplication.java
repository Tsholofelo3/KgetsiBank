package bw.ac.kgetsibank;

import bw.ac.kgetsibank.controller.LoginController;

import bw.ac.kgetsibank.model.User;

import bw.ac.kgetsibank.util.Database;

import bw.ac.kgetsibank.view.DashboardView;
import bw.ac.kgetsibank.view.LoginView;

import javafx.application.Application;

import javafx.scene.Scene;

import javafx.stage.Stage;

public class KgetsiBankApplication
        extends Application {

    private Stage primaryStage;

    private final LoginController
            loginController =
            new LoginController();

    @Override
    public void start(
            Stage stage
    ) {

        Database.initialize();

        this.primaryStage = stage;

        primaryStage.setMinWidth(
                900
        );

        primaryStage.setMinHeight(
                600
        );

        showLogin();

        primaryStage.show();
    }

    private void showLogin() {

        LoginView loginView =
                new LoginView(
                        loginController,
                        this::showDashboard
                );

        Scene scene =
                new Scene(
                        loginView.getRoot(),
                        1000,
                        680
                );

        addStylesheet(scene);

        primaryStage.setTitle(
                "KgetsiBank - Login"
        );

        primaryStage.setScene(
                scene
        );

        primaryStage.centerOnScreen();
    }

    private void showDashboard(
            User user
    ) {

        DashboardView dashboard =
                new DashboardView(
                        user,
                        this::logout
                );

        Scene scene =
                new Scene(
                        dashboard.getRoot(),
                        1200,
                        760
                );

        addStylesheet(scene);

        primaryStage.setTitle(
                "KgetsiBank - Dashboard"
        );

        primaryStage.setScene(
                scene
        );

        primaryStage.centerOnScreen();
    }

    private void logout() {

        loginController.logout();

        showLogin();
    }

    private void addStylesheet(
            Scene scene
    ) {

        var stylesheet =
                getClass().getResource(
                        "/css/kgetsibank.css"
                );

        if (stylesheet != null) {

            scene.getStylesheets()
                    .add(
                            stylesheet
                                    .toExternalForm()
                    );
        }
    }

    public static void main(
            String[] args
    ) {

        launch(args);
    }
}