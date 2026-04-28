package com.example.messenger.controller;

import com.example.messenger.service.AsyncOperationService;
import com.example.messenger.service.ConcurrencyDemoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lab")
public final class LabConcurrencyController {

    private final AsyncOperationService asyncService;
    private final ConcurrencyDemoService concurrencyService;

    public LabConcurrencyController(
            final AsyncOperationService asyncService,
            final ConcurrencyDemoService concurrencyService) {
        this.asyncService = asyncService;
        this.concurrencyService = concurrencyService;
    }

    @PostMapping("/async/start")
    public ResponseEntity<String> startAsyncTask() {
        final String taskId = asyncService.startAsyncTask();
        return ResponseEntity.ok("Задача запущена. ID: " + taskId);
    }

    @GetMapping("/async/status/{taskId}")
    public ResponseEntity<String> checkStatus(final @PathVariable String taskId) {
        return ResponseEntity.ok("Статус задачи: " + asyncService.checkTaskStatus(taskId));
    }

    @GetMapping("/concurrency/unsafe")
    public ResponseEntity<String> testUnsafeCounter() throws InterruptedException {
        final int result = concurrencyService.runUnsafeCounter();
        return ResponseEntity.ok("Ожидалось: 1000, Фактически получилось: " + result
                + " (Произошла потеря данных из-за Race Condition)");
    }

    @GetMapping("/concurrency/safe")
    public ResponseEntity<String> testSafeCounter() throws InterruptedException {
        final int result = concurrencyService.runSafeCounter();
        return ResponseEntity.ok("Ожидалось: 1000, Фактически получилось: " + result
                + " (Данные спасены с помощью AtomicInteger)");
    }
}