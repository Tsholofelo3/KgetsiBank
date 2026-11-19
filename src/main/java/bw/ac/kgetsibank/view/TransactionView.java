package bw.ac.kgetsibank.view;

import bw.ac.kgetsibank.controller.*;

import bw.ac.kgetsibank.model.*;

import javafx.collections.FXCollections;

import javafx.geometry.Insets;

import javafx.scene.Parent;

import javafx.scene.control.*;

import javafx.scene.layout.*;

import java.util.function.Consumer;

public class TransactionView {

    private final CustomerController customers =
            new CustomerController();

    private final AccountController accounts =
            new AccountController();

    private final TransactionController transactions =
            new TransactionController();

    private final Consumer<String> status;

    private final BorderPane root =
            new BorderPane();

    private final ComboBox<Customer> customerBox =
            new ComboBox<>();

    private final ComboBox<Account> accountBox =
            new ComboBox<>();

    private final TextField amount =
            new TextField();

    private final TextField description =
            new TextField();

    private final ListView<String> history =
            new ListView<>();

    private final Label message =
            new Label();

    public TransactionView(
            Consumer<String> status
    ) {

        this.status = status;

        build();
        loadCustomers();
    }

    private void build() {

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
                new Label("Account"),
                0,
                1
        );

        form.add(
                accountBox,
                1,
                1
        );

        form.add(
                new Label("Amount"),
                0,
                2
        );

        form.add(
                amount,
                1,
                2
        );

        form.add(
                new Label("Description"),
                0,
                3
        );

        form.add(
                description,
                1,
                3
        );

        Button deposit =
                new Button(
                        "Deposit"
                );

        Button withdraw =
                new Button(
                        "Withdraw"
                );

        deposit.setOnAction(
                event -> deposit()
        );

        withdraw.setOnAction(
                event -> withdraw()
        );

        HBox actions =
                new HBox(
                        10,
                        deposit,
                        withdraw
                );

        VBox left =
                new VBox(
                        15,
                        new Label(
                                "Transactions"
                        ),
                        form,
                        actions,
                        message
                );

        left.setPadding(
                new Insets(20)
        );

        VBox right =
                new VBox(
                        12,
                        new Label(
                                "Transaction History"
                        ),
                        history
                );

        right.setPadding(
                new Insets(20)
        );

        VBox.setVgrow(
                history,
                Priority.ALWAYS
        );

        root.setLeft(left);
        root.setCenter(right);

        customerBox.setOnAction(
                event ->
                        loadAccounts()
        );

        accountBox.setOnAction(
                event ->
                        loadHistory()
        );
    }

    private void loadCustomers() {

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

            accountBox.setItems(
                    FXCollections
                            .observableArrayList(
                                    accounts
                                            .getCustomerAccounts(
                                                    customer
                                                            .getId()
                                            )
                            )
            );

            if (!accountBox
                    .getItems()
                    .isEmpty()) {

                accountBox
                        .getSelectionModel()
                        .selectFirst();

                loadHistory();
            }

        } catch (Exception exception) {

            message.setText(
                    exception.getMessage()
            );
        }
    }

    private void deposit() {

        Account account =
                accountBox.getValue();

        if (account == null) {

            message.setText(
                    "Select an account."
            );

            return;
        }

        try {

            Account updated =
                    transactions.deposit(
                            account.getAccountNumber(),
                            amount.getText(),
                            description.getText()
                    );

            message.setText(
                    "Deposit successful. Balance: BWP "
                            + updated.getBalance()
            );

            status.accept(
                    "Deposit completed successfully."
            );

            amount.clear();
            description.clear();

            loadAccounts();

        } catch (Exception exception) {

            message.setText(
                    exception.getMessage()
            );
        }
    }

    private void withdraw() {

        Account account =
                accountBox.getValue();

        if (account == null) {

            message.setText(
                    "Select an account."
            );

            return;
        }

        try {

            Account updated =
                    transactions.withdraw(
                            account.getAccountNumber(),
                            amount.getText(),
                            description.getText()
                    );

            message.setText(
                    "Withdrawal successful. Balance: BWP "
                            + updated.getBalance()
            );

            status.accept(
                    "Withdrawal completed successfully."
            );

            amount.clear();
            description.clear();

            loadAccounts();

        } catch (Exception exception) {

            message.setText(
                    exception.getMessage()
            );
        }
    }

    private void loadHistory() {

        Account account =
                accountBox.getValue();

        history.getItems().clear();

        if (account == null) {
            return;
        }

        try {

            for (BankTransaction transaction :
                    transactions
                            .getTransactionHistory(
                                    account.getId()
                            )) {

                history.getItems()
                        .add(
                                transaction.transactionTime()
                                        + " | "
                                        + transaction.type()
                                        + " | BWP "
                                        + transaction.amount()
                                        + " | Balance: BWP "
                                        + transaction.balanceAfter()
                                        + " | "
                                        + (
                                        transaction.description()
                                                == null
                                                ? ""
                                                : transaction.description()
                                )
                        );
            }

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