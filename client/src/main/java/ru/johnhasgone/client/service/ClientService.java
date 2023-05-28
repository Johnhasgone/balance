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
import java.util.stream.LongStream;

@Service
public class ClientService {

    @Value("${client.thread-count}")
    private int threadCount;
    @Value("${client.read-quota}")
    private int readQuota;
    @Value("${client.write-quota}")
    private int writeQuota;
    @Value("${client.id-to-index}")
    private int toIndex;

    private final List<Long> readIdList = LongStream.range(1L, 101L)
            .boxed()
            .toList();
    private final List<Long> writeIdList = LongStream.range(1L, 101L)
            .boxed()
            .toList();

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://balance:8080")
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
                        getBalance(randomFromList(readIdList, toIndex));
                    } else {
                        changeBalance(randomFromList(writeIdList, toIndex), BigDecimal.ONE);
                    }
                }
            });
        }
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

    private Long randomFromList(List<Long> list, int toIndex) {
        return list.get(ThreadLocalRandom.current().nextInt(Math.min(list.size(), toIndex)));
    }
}
