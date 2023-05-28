package ru.johnhasgone.balance.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(Long id, BigDecimal balance, BigDecimal amount) {
        super("Insufficient funds for id = " + id + ": balance = " + balance + ", amount to withdraw = " + amount);
    }
}