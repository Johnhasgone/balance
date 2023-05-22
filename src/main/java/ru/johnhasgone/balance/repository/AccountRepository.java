package ru.johnhasgone.balance.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.johnhasgone.balance.domain.Account;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    // thread gets lock for a specific account, so other threads can't read or write account info
    // until needed operation with account executed and the lock released.
    // It's not so good for perfomance, but helpful to all the users get the consistent and actual account balance info
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.id = :id")
    Optional<Account> findByIdWithLock(@Param("id") Long id);
}
