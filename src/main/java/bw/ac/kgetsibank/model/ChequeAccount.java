package bw.ac.kgetsibank.model;

import java.math.BigDecimal;

public class ChequeAccount extends Account {

    private String employerName;
    private String employerAddress;

    public ChequeAccount(
            long customerId,
            String accountNumber,
            BigDecimal openingBalance,
            String branch,
            boolean employed,
            String employerName,
            String employerAddress
    ) {

        super(
                customerId,
                accountNumber,
                openingBalance,
                branch
        );

        if (!employed) {

            throw new IllegalArgumentException(
                    "Customer must be employed "
                            + "to open a cheque account."
            );
        }

        if (employerName == null ||
                employerName.isBlank()) {

            throw new IllegalArgumentException(
                    "Employer name is required."
            );
        }

        if (employerAddress == null ||
                employerAddress.isBlank()) {

            throw new IllegalArgumentException(
                    "Employer address is required."
            );
        }

        this.employerName =
                employerName.trim();

        this.employerAddress =
                employerAddress.trim();
    }

    @Override
    public void withdraw(
            BigDecimal amount
    ) {

        standardWithdraw(amount);
    }

    @Override
    public AccountType getAccountType() {

        return AccountType.CHEQUE;
    }

    public String getEmployerName() {
        return employerName;
    }

    public void setEmployerName(
            String employerName
    ) {

        if (employerName == null ||
                employerName.isBlank()) {

            throw new IllegalArgumentException(
                    "Employer name is required."
            );
        }

        this.employerName =
                employerName.trim();
    }

    public String getEmployerAddress() {
        return employerAddress;
    }

    public void setEmployerAddress(
            String employerAddress
    ) {

        if (employerAddress == null ||
                employerAddress.isBlank()) {

            throw new IllegalArgumentException(
                    "Employer address is required."
            );
        }

        this.employerAddress =
                employerAddress.trim();
    }
}