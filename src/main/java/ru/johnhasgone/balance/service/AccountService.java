package ru.johnhasgone.balance.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.johnhasgone.balance.domain.Account;
import ru.johnhasgone.balance.exception.AccountNotFoundException;
import ru.johnhasgone.balance.exception.InsufficientFundsException;
import ru.johnhasgone.balance.repository.AccountRepository;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AccountService {
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    @Autowired
    private BalanceRequestCounter counter;

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Cacheable(value = "accounts", key = "#id", unless = "#result == null")
    public BigDecimal getBalance(Long id) {
        BigDecimal balance = accountRepository.findById(id)
                .map(Account::getAmount)
                .orElseThrow(() -> new AccountNotFoundException(id));
        counter.addGetRequest();
        return balance;
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
        counter.addChangeRequest();
    }
}
