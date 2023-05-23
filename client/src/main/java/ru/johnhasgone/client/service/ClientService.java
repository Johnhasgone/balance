package ru.johnhasgone.client.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
public class ClientService {

    @Value("${client.thread-count}")
    private int threadCount;
    @Value("${client.read-quota}")
    private int readQuota;
    @Value("${client.write-quota}")
    private int writeQuota;

    private final List<Long> readIdList = List.of(1L);
    private final List<Long> writeIdList = List.of(1L);

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080")
            .build();

    @EventListener(ApplicationReadyEvent.class)
    public void sendRequest() throws InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            threadPool.execute(() -> {
                while (true) {
                    // вероятность вызова метода getBalance
                    double readProbability = (double) readQuota / (double) (readQuota + writeQuota);

                    if (ThreadLocalRandom.current().nextDouble() < readProbability) {
                        getBalance(randomFromList(readIdList));
                    } else {
                        BigDecimal delta = BigDecimal.valueOf(ThreadLocalRandom.current().nextInt(1, 101));
                        changeBalance(randomFromList(writeIdList), BigDecimal.TEN);
                    }
                }
            });
        }
        threadPool.awaitTermination(10000, TimeUnit.SECONDS);
    }

    public void getBalance(Long id) {
        BigDecimal balance = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/{id}/balance")
                        .build(id)
                ).retrieve()
                .bodyToMono(BigDecimal.class)
                .block();
    }

    public void changeBalance(Long id, BigDecimal delta) {

        webClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/{id}/balance")
                        .build(id)
                ).bodyValue(delta)
                .retrieve()
                .toBodilessEntity()
                .block();

    }

    private Long randomFromList(List<Long> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }
}
