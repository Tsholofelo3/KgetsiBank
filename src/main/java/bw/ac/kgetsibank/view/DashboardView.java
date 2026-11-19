package bw.ac.kgetsibank.view;

import bw.ac.kgetsibank.model.User;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Parent;

import javafx.scene.control.*;

import javafx.scene.layout.*;

public class DashboardView {

    private final BorderPane root =
            new BorderPane();

    private final User user;

    private final Runnable logout;

    private final Label status =
            new Label(
                    "System ready."
            );

    public DashboardView(
            User user,
            Runnable logout
    ) {

        this.user = user;
        this.logout = logout;

        build();
    }

    private void build() {

        root.getStyleClass()
                .add(
                        "app-background"
                );

        root.setTop(
                createHeader()
        );

        TabPane tabs =
                new TabPane();

        tabs.setTabClosingPolicy(
                TabPane
                        .TabClosingPolicy
                        .UNAVAILABLE
        );

        CustomerView customers =
                new CustomerView(
                        this::setStatus
                );

        AccountView accounts =
                new AccountView(
                        this::setStatus
                );

        TransactionView transactions =
                new TransactionView(
                        this::setStatus
                );

        tabs.getTabs().addAll(
                new Tab(
                        "Customers",
                        customers.getRoot()
                ),
                new Tab(
                        "Accounts",
                        accounts.getRoot()
                ),
                new Tab(
                        "Transactions",
                        transactions.getRoot()
                )
        );

        root.setCenter(tabs);

        HBox statusBar =
                new HBox(status);

        statusBar.setPadding(
                new Insets(8, 15, 8, 15)
        );

        statusBar.getStyleClass()
                .add(
                        "status-bar"
                );

        root.setBottom(
                statusBar
        );
    }

    private Parent createHeader() {

        Label logo =
                new Label(
                        "KGETSIBANK"
                );

        logo.getStyleClass()
                .add(
                        "header-title"
                );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label account =
                new Label(
                        user.getUsername()
                                + " | "
                                + user.getRole()
                );

        account.getStyleClass()
                .add(
                        "header-user"
                );

        Button logoutButton =
                new Button(
                        "Logout"
                );

        logoutButton.setOnAction(
                event -> logout.run()
        );

        HBox header =
                new HBox(
                        15,
                        logo,
                        spacer,
                        account,
                        logoutButton
                );

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        header.setPadding(
                new Insets(16, 22, 16, 22)
        );

        header.getStyleClass()
                .add(
                        "app-header"
                );

        return header;
    }

    private void setStatus(
            String text
    ) {

        status.setText(
                text == null
                        ? ""
                        : text
        );
    }

    public Parent getRoot() {

        return root;
    }
}