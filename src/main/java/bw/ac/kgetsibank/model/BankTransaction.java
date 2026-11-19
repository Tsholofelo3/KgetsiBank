package bw.ac.kgetsibank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BankTransaction(

        long id,

        long accountId,

        TransactionType type,

        BigDecimal amount,

        LocalDateTime transactionTime,

        String description,

        BigDecimal balanceAfter

) {

    public BankTransaction {

        if (accountId <= 0) {

            throw new IllegalArgumentException(
                    "A valid account ID is required."
            );
        }

        if (type == null) {

            throw new IllegalArgumentException(
                    "Transaction type is required."
            );
        }

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Transaction amount cannot be negative."
            );
        }

        if (transactionTime == null) {
            transactionTime = LocalDateTime.now();
        }

        if (balanceAfter == null) {

            throw new IllegalArgumentException(
                    "Resulting balance is required."
            );
        }
    }
}