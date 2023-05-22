package ru.johnhasgone.balance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.johnhasgone.balance.exception.InsufficientFundsException;
import ru.johnhasgone.balance.service.AccountService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{id}/balance")
    public BigDecimal getBalance(@PathVariable Long id) {
        return accountService.getBalance(id);
    }

    @PostMapping("/{id}/balance")
    public void changeBalance(@PathVariable Long id, @RequestBody BigDecimal delta) {
        accountService.changeBalance(id, delta);
    }
}
