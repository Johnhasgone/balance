CREATE TABLE account
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    amount       NUMERIC(18, 2) NOT NULL,
    last_updated TIMESTAMP      NOT NULL
);

INSERT INTO account (amount, last_updated)
SELECT (id * 10), current_timestamp
FROM generate_series(1, 10000) AS id;