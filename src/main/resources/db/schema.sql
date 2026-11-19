CREATE TABLE IF NOT EXISTS customers (
                                         id INTEGER PRIMARY KEY AUTOINCREMENT,

                                         customer_number TEXT NOT NULL UNIQUE,
                                         first_name TEXT NOT NULL,
                                         surname TEXT NOT NULL,
                                         national_id TEXT NOT NULL UNIQUE,

                                         address TEXT,
                                         phone TEXT,
                                         email TEXT,

                                         employed INTEGER NOT NULL DEFAULT 0,

                                         employer_name TEXT,
                                         employer_address TEXT,

                                         active INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS users (
                                     id INTEGER PRIMARY KEY AUTOINCREMENT,

                                     username TEXT NOT NULL UNIQUE,
                                     password_hash TEXT NOT NULL,

                                     role TEXT NOT NULL
                                     CHECK (
                                     role IN (
                                     'ADMIN',
                                     'OFFICER',
                                     'CUSTOMER'
)
    ),

    customer_id INTEGER,

    active INTEGER NOT NULL DEFAULT 1,

    FOREIGN KEY (customer_id)
    REFERENCES customers(id)
    );

CREATE TABLE IF NOT EXISTS accounts (
                                        id INTEGER PRIMARY KEY AUTOINCREMENT,

                                        customer_id INTEGER NOT NULL,

                                        account_number TEXT NOT NULL UNIQUE,

                                        account_type TEXT NOT NULL
                                        CHECK (
                                        account_type IN (
                                        'SAVINGS',
                                        'INVESTMENT',
                                        'CHEQUE'
)
    ),

    balance NUMERIC NOT NULL DEFAULT 0.00,

    branch TEXT NOT NULL,

    status TEXT NOT NULL
    CHECK (
              status IN (
              'ACTIVE',
              'SUSPENDED',
              'CLOSED'
                        )
    ),

    date_opened TEXT NOT NULL,

    FOREIGN KEY (customer_id)
    REFERENCES customers(id)
    );

CREATE TABLE IF NOT EXISTS transactions (
                                            id INTEGER PRIMARY KEY AUTOINCREMENT,

                                            account_id INTEGER NOT NULL,

                                            transaction_type TEXT NOT NULL
                                            CHECK (
                                            transaction_type IN (
                                            'OPENING_DEPOSIT',
                                            'DEPOSIT',
                                            'WITHDRAWAL',
                                            'INTEREST',
                                            'REVERSAL'
)
    ),

    amount NUMERIC NOT NULL,

    transaction_time TEXT NOT NULL,

    description TEXT,

    balance_after NUMERIC NOT NULL,

    FOREIGN KEY (account_id)
    REFERENCES accounts(id)
    );

CREATE INDEX IF NOT EXISTS idx_accounts_customer
    ON accounts(customer_id);

CREATE INDEX IF NOT EXISTS idx_transactions_account
    ON transactions(account_id);

CREATE INDEX IF NOT EXISTS idx_transactions_time
    ON transactions(transaction_time);