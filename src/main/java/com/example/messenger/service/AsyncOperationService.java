package com.example.messenger.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AsyncOperationService {

    private final Map<String, String> taskStatuses = new ConcurrentHashMap<>();
    private final AsyncOperationService self;

    public AsyncOperationService(@Lazy final AsyncOperationService self) {
        this.self = self;
    }

    public String startAsyncTask() {
        final String taskId = UUID.randomUUID().toString();

        taskStatuses.put(taskId, "RUNNING");

        self.processHeavyTask(taskId);

        return taskId;
    }

    @Async
    public CompletableFuture<Void> processHeavyTask(final String taskId) {
        try {

            Thread.sleep(10000);

            taskStatuses.put(taskId, "COMPLETED");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            taskStatuses.put(taskId, "ERROR: Процесс был прерван");
        }

        return CompletableFuture.completedFuture(null);
    }

    public String checkTaskStatus(final String taskId) {
        return taskStatuses.getOrDefault(taskId, "NOT_FOUND: Задача не найдена");
    }
}