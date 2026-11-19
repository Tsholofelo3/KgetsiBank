package bw.ac.kgetsibank.view;

import bw.ac.kgetsibank.controller.AccountController;
import bw.ac.kgetsibank.controller.CustomerController;

import bw.ac.kgetsibank.model.*;

import javafx.collections.FXCollections;

import javafx.geometry.Insets;

import javafx.scene.Parent;

import javafx.scene.control.*;

import javafx.scene.layout.*;

import java.util.function.Consumer;

public class AccountView {

    private final AccountController accounts =
            new AccountController();

    private final CustomerController customers =
            new CustomerController();

    private final Consumer<String> status;

    private final BorderPane root =
            new BorderPane();

    private final ComboBox<Customer> customerBox =
            new ComboBox<>();

    private final ComboBox<AccountType> typeBox =
            new ComboBox<>();

    private final TextField openingBalance =
            new TextField("0.00");

    private final TextField branch =
            new TextField(
                    "Main Branch"
            );

    private final ListView<Account> accountList =
            new ListView<>();

    private final Label message =
            new Label();

    public AccountView(
            Consumer<String> status
    ) {

        this.status = status;

        build();
        load();
    }

    private void build() {

        typeBox.setItems(
                FXCollections
                        .observableArrayList(
                                AccountType.values()
                        )
        );

        typeBox.getSelectionModel()
                .selectFirst();

        GridPane form =
                new GridPane();

        form.setHgap(10);
        form.setVgap(10);

        form.add(
                new Label("Customer"),
                0,
                0
        );

        form.add(
                customerBox,
                1,
                0
        );

        form.add(
                new Label("Account Type"),
                0,
                1
        );

        form.add(
                typeBox,
                1,
                1
        );

        form.add(
                new Label("Opening Balance"),
                0,
                2
        );

        form.add(
                openingBalance,
                1,
                2
        );

        form.add(
                new Label("Branch"),
                0,
                3
        );

        form.add(
                branch,
                1,
                3
        );

        Button open =
                new Button(
                        "Open Account"
                );

        open.setOnAction(
                event ->
                        openAccount()
        );

        Button interest =
                new Button(
                        "Process Monthly Interest"
                );

        interest.setOnAction(
                event ->
                        processInterest()
        );

        VBox left =
                new VBox(
                        15,
                        new Label(
                                "Account Management"
                        ),
                        form,
                        open,
                        interest,
                        message
                );

        left.setPadding(
                new Insets(20)
        );

        VBox right =
                new VBox(
                        12,
                        new Label(
                                "Customer Accounts"
                        ),
                        accountList
                );

        right.setPadding(
                new Insets(20)
        );

        VBox.setVgrow(
                accountList,
                Priority.ALWAYS
        );

        root.setLeft(left);
        root.setCenter(right);

        customerBox.setOnAction(
                event ->
                        loadAccounts()
        );
    }

    private void load() {

        try {

            customerBox.setItems(
                    FXCollections
                            .observableArrayList(
                                    customers
                                            .getAllCustomers()
                            )
            );

            if (!customerBox
                    .getItems()
                    .isEmpty()) {

                customerBox
                        .getSelectionModel()
                        .selectFirst();

                loadAccounts();
            }

        } catch (Exception exception) {

            message.setText(
                    exception.getMessage()
            );
        }
    }

    private void loadAccounts() {

        Customer customer =
                customerBox.getValue();

        if (customer == null) {
            return;
        }

        try {

            accountList.setItems(
                    FXCollections
                            .observableArrayList(
                                    accounts
                                            .getCustomerAccounts(
                                                    customer
                                                            .getId()
                                            )
                            )
            );

        } catch (Exception exception) {

            message.setText(
                    exception.getMessage()
            );
        }
    }

    private void openAccount() {

        try {

            Customer customer =
                    customerBox.getValue();

            if (customer == null) {

                throw new IllegalArgumentException(
                        "Select a customer."
                );
            }

            Account account =
                    accounts.openAccount(
                            customer,
                            typeBox.getValue(),
                            openingBalance.getText(),
                            branch.getText()
                    );

            message.setText(
                    "Opened: "
                            + account
                            .getAccountNumber()
            );

            status.accept(
                    "Account opened successfully."
            );

            loadAccounts();

        } catch (Exception exception) {

            message.setText(
                    exception.getMessage()
            );
        }
    }

    private void processInterest() {

        try {

            int count =
                    accounts
                            .processMonthlyInterest();

            message.setText(
                    "Interest processed for "
                            + count
                            + " account(s)."
            );

            status.accept(
                    "Monthly interest processing completed."
            );

            loadAccounts();

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