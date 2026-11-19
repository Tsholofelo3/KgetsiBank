package bw.ac.kgetsibank.view;

import bw.ac.kgetsibank.controller.CustomerController;

import bw.ac.kgetsibank.model.Customer;

import javafx.collections.FXCollections;

import javafx.geometry.Insets;

import javafx.scene.Parent;

import javafx.scene.control.*;

import javafx.scene.layout.*;

import java.util.function.Consumer;

public class CustomerView {

    private final CustomerController controller =
            new CustomerController();

    private final Consumer<String> status;

    private final BorderPane root =
            new BorderPane();

    private final TextField number =
            new TextField();

    private final TextField firstName =
            new TextField();

    private final TextField surname =
            new TextField();

    private final TextField nationalId =
            new TextField();

    private final TextField address =
            new TextField();

    private final TextField phone =
            new TextField();

    private final TextField email =
            new TextField();

    private final CheckBox employed =
            new CheckBox(
                    "Customer is employed"
            );

    private final TextField employer =
            new TextField();

    private final TextField employerAddress =
            new TextField();

    private final ListView<Customer> customers =
            new ListView<>();

    private final Label message =
            new Label();

    public CustomerView(
            Consumer<String> status
    ) {

        this.status = status;

        build();
        refresh();
    }

    private void build() {

        GridPane form =
                new GridPane();

        form.setHgap(10);
        form.setVgap(10);

        int r = 0;

        add(
                form,
                r++,
                "Customer Number",
                number
        );

        add(
                form,
                r++,
                "First Name",
                firstName
        );

        add(
                form,
                r++,
                "Surname",
                surname
        );

        add(
                form,
                r++,
                "National ID",
                nationalId
        );

        add(
                form,
                r++,
                "Address",
                address
        );

        add(
                form,
                r++,
                "Phone",
                phone
        );

        add(
                form,
                r++,
                "Email",
                email
        );

        form.add(
                employed,
                1,
                r++
        );

        add(
                form,
                r++,
                "Employer",
                employer
        );

        add(
                form,
                r++,
                "Employer Address",
                employerAddress
        );

        employer.setDisable(true);

        employerAddress.setDisable(
                true
        );

        employed.selectedProperty()
                .addListener(
                        (
                                observable,
                                oldValue,
                                value
                        ) -> {

                            employer
                                    .setDisable(
                                            !value
                                    );

                            employerAddress
                                    .setDisable(
                                            !value
                                    );
                        }
                );

        Button register =
                new Button(
                        "Register Customer"
                );

        register.setOnAction(
                event -> register()
        );

        form.add(
                register,
                1,
                r++
        );

        form.add(
                message,
                1,
                r
        );

        VBox left =
                new VBox(
                        12,
                        new Label(
                                "Register Customer"
                        ),
                        form
                );

        left.setPadding(
                new Insets(20)
        );

        VBox right =
                new VBox(
                        12,
                        new Label(
                                "Registered Customers"
                        ),
                        customers
                );

        right.setPadding(
                new Insets(20)
        );

        VBox.setVgrow(
                customers,
                Priority.ALWAYS
        );

        root.setLeft(left);
        root.setCenter(right);
    }

    private void add(
            GridPane pane,
            int row,
            String label,
            TextField field
    ) {

        field.setPrefWidth(
                260
        );

        pane.add(
                new Label(label),
                0,
                row
        );

        pane.add(
                field,
                1,
                row
        );
    }

    private void register() {

        try {

            Customer customer =
                    controller.registerCustomer(
                            number.getText(),
                            firstName.getText(),
                            surname.getText(),
                            nationalId.getText(),
                            address.getText(),
                            phone.getText(),
                            email.getText(),
                            employed.isSelected(),
                            employer.getText(),
                            employerAddress.getText()
                    );

            message.setText(
                    "Registered: "
                            + customer
                            .getFullName()
            );

            status.accept(
                    "Customer registered successfully."
            );

            clear();
            refresh();

        } catch (Exception exception) {

            message.setText(
                    exception.getMessage()
            );
        }
    }

    private void refresh() {

        try {

            customers.setItems(
                    FXCollections
                            .observableArrayList(
                                    controller
                                            .getAllCustomers()
                            )
            );

        } catch (Exception exception) {

            message.setText(
                    exception.getMessage()
            );
        }
    }

    private void clear() {

        number.clear();
        firstName.clear();
        surname.clear();
        nationalId.clear();
        address.clear();
        phone.clear();
        email.clear();

        employed.setSelected(false);

        employer.clear();
        employerAddress.clear();
    }

    public Parent getRoot() {

        return root;
    }
}