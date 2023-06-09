CREATE TABLE IF NOT EXISTS account
(
    id           BIGSERIAL PRIMARY KEY,
    amount       NUMERIC(18, 2) NOT NULL,
    last_updated TIMESTAMP      NOT NULL
);

INSERT INTO account (amount, last_updated)
SELECT 0, current_timestamp
FROM generate_series(1, 100) AS id;