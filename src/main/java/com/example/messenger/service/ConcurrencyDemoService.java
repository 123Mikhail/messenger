package com.example.messenger.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public final class ConcurrencyDemoService {

    public int runUnsafeCounter() throws InterruptedException {
        final UnsafeCounter counter = new UnsafeCounter();
        final ExecutorService executor = Executors.newFixedThreadPool(50);
        final CountDownLatch latch = new CountDownLatch(1000);

        for (int i = 0; i < 1000; i++) {
            executor.submit(() -> {
                try {
                    Thread.sleep(2);
                    counter.increment();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();
        return counter.count;
    }

    public int runSafeCounter() throws InterruptedException {
        final AtomicInteger safeCounter = new AtomicInteger(0);
        final ExecutorService executor = Executors.newFixedThreadPool(50);
        final CountDownLatch latch = new CountDownLatch(1000);

        for (int i = 0; i < 1000; i++) {
            executor.submit(() -> {
                try {
                    Thread.sleep(2);
                    safeCounter.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();
        return safeCounter.get();
    }

    private static class UnsafeCounter {
        int count = 0;

        void increment() {
            count++;
        }
    }
}