package ru.johnhasgone.balance.service;

import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.johnhasgone.balance.domain.Account;
import ru.johnhasgone.balance.exception.AccountNotFoundException;
import ru.johnhasgone.balance.exception.InsufficientFundsException;
import ru.johnhasgone.balance.repository.AccountRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Cacheable(value = "accounts", key = "#id", unless = "#result == null")
    public BigDecimal getBalance(Long id) {
        return accountRepository.findById(id)
                .map(Account::getAmount)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    @Transactional
    @CacheEvict(value = "accounts", key = "#id")
    public void changeBalance(Long id, BigDecimal delta) {
        Account account = accountRepository.findByIdWithLock(id)
                .orElseThrow(() -> new AccountNotFoundException(id ));

        BigDecimal newAmount = account.getAmount().add(delta);

        if (newAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException(id, account.getAmount(), delta);
        }

        account.setAmount(newAmount);
        account.setLastUpdated(LocalDateTime.now());

        accountRepository.save(account);
    }
}
