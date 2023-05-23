package ru.johnhasgone.balance.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class BalanceRequestCounter {
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final AtomicLong getBalanceCount = new AtomicLong(0);
    private final AtomicLong changeBalanceCount = new AtomicLong(0);

    private long prevRequestCount;


    public void addGetRequest() {
        getBalanceCount.incrementAndGet();
    }

    public void addChangeRequest() {
        changeBalanceCount.incrementAndGet();
    }

    @Scheduled(fixedRate = 1000)
    private void logRequestCount() {
        long getCount = getBalanceCount.get();
        long changeCount = changeBalanceCount.get();
        long requestCount = getCount + changeCount;

        log.info("get - {}, change - {}, rps - {}", getCount, changeCount, requestCount - prevRequestCount);

        prevRequestCount = requestCount;
    }
}
