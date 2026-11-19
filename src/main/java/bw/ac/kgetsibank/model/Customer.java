package bw.ac.kgetsibank.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Customer {

    private long id;

    private String customerNumber;
    private String firstName;
    private String surname;
    private String nationalId;
    private String address;
    private String phone;
    private String email;

    private boolean employed;

    private String employerName;
    private String employerAddress;

    private boolean active;

    private final List<Account> accounts =
            new ArrayList<>();

    public Customer() {
        this.active = true;
    }

    public Customer(
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
    ) {

        setCustomerNumber(customerNumber);
        setFirstName(firstName);
        setSurname(surname);
        setNationalId(nationalId);

        this.address = address;
        this.phone = phone;
        this.email = email;

        this.employed = employed;
        this.employerName = employerName;
        this.employerAddress = employerAddress;

        this.active = true;
    }

    public void addAccount(
            Account account
    ) {

        if (account == null) {

            throw new IllegalArgumentException(
                    "Account cannot be null."
            );
        }

        accounts.add(account);
    }

    public List<Account> getAccounts() {

        return Collections.unmodifiableList(
                accounts
        );
    }

    public String getFullName() {

        return firstName
                + " "
                + surname;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(
            String customerNumber
    ) {

        requireText(
                customerNumber,
                "Customer number"
        );

        this.customerNumber =
                customerNumber.trim();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(
            String firstName
    ) {

        requireText(
                firstName,
                "First name"
        );

        this.firstName =
                firstName.trim();
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(
            String surname
    ) {

        requireText(
                surname,
                "Surname"
        );

        this.surname =
                surname.trim();
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(
            String nationalId
    ) {

        requireText(
                nationalId,
                "National ID"
        );

        this.nationalId =
                nationalId.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(
            String address
    ) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(
            String phone
    ) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(
            String email
    ) {
        this.email = email;
    }

    public boolean isEmployed() {
        return employed;
    }

    public void setEmployed(
            boolean employed
    ) {
        this.employed = employed;
    }

    public String getEmployerName() {
        return employerName;
    }

    public void setEmployerName(
            String employerName
    ) {
        this.employerName = employerName;
    }

    public String getEmployerAddress() {
        return employerAddress;
    }

    public void setEmployerAddress(
            String employerAddress
    ) {
        this.employerAddress = employerAddress;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(
            boolean active
    ) {
        this.active = active;
    }

    private static void requireText(
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

    @Override
    public String toString() {

        return customerNumber
                + " - "
                + getFullName();
    }
}