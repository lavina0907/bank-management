CREATE TABLE IF NOT EXISTS customers
(
    customer_id UUID PRIMARY KEY,
    first_name  VARCHAR(100)        NOT NULL,
    last_name   VARCHAR(100)        NOT NULL,
    email       VARCHAR(255) UNIQUE NOT NULL,
    phone       VARCHAR(20) UNIQUE  NOT NULL,
    created_at  TIMESTAMP
);
CREATE TABLE IF NOT EXISTS accounts
(
    account_id  UUID PRIMARY KEY,
    customer_id UUID NOT NULL REFERENCES customers (customer_id),
    created_at  TIMESTAMP
);
CREATE TABLE IF NOT EXISTS account_balances
(
    account_id    UUID           NOT NULL REFERENCES accounts (account_id),
    currency_code VARCHAR(3)     NOT NULL,
    balance       DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (account_id, currency_code)
);
CREATE TABLE IF NOT EXISTS transactions
(
    transaction_id   UUID PRIMARY KEY,
    account_id       UUID        NOT NULL REFERENCES accounts (account_id),
    currency_code    VARCHAR(3)  NOT NULL,
    amount           DECIMAL(18, 2),
    transaction_type VARCHAR(10) NOT NULL,
    transaction_date TIMESTAMP,
    status           VARCHAR(20)
);
CREATE TABLE IF NOT EXISTS exchange_rates
(
    base_currency   VARCHAR(3)     NOT NULL,
    target_currency VARCHAR(3)     NOT NULL,
    rate            DECIMAL(10, 4) NOT NULL,
    PRIMARY KEY (base_currency, target_currency)
);
