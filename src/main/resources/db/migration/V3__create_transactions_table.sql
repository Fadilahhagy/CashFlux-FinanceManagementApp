CREATE TABLE transactions (
    id BIGINT GENERATED  BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    type VARCHAR(10) NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    description VARCHAR(255),
    transaction_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_transactions_user
                          FOREIGN KEY (user_id)
                          REFERENCES users(id)
                          ON DELETE CASCADE,
    CONSTRAINT fk_transactions_category
                          FOREIGN KEY (category_id)
                          REFERENCES categories(id)
                          ON DELETE RESTRICT,
    CONSTRAINT chk_transaction_type
                          CHECK(type IN ('INCOME', 'EXPENSE')),
    CONSTRAINT chk_transaction_amount
                          CHECK ( amount > 0 )
);

CREATE INDEX idx_transactions_user_date
    ON transactions (user_id, transaction_date DESC);

CREATE INDEX idx_transactions_category
    ON transactions (category_id);