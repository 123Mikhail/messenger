package com.example.messenger.controller;

import com.example.messenger.service.LabDemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final LabDemoService labDemoService;

    @GetMapping("/without-tx")
    public String testWithoutTx() {
        try {
            labDemoService.saveDataWithoutTransaction();
        } catch (Exception e) {
            return "Без @Transactional. Исключение: " + e.getMessage() + " Проверьте БД (юзер hacker_no_tx сохранился).";
        }
        return "Успех";
    }

    @GetMapping("/with-tx")
    public String testWithTx() {
        try {
            labDemoService.saveDataWithTransaction();
        } catch (Exception e) {
            return "С @Transactional. Исключение: " + e.getMessage() + " Проверьте БД (ROLLBACK: юзер hacker_with_tx НЕ сохранился).";
        }
        return "Успех";
    }

    @GetMapping("/n-plus-one-problem")
    public String nPlusOneProblem() {
        labDemoService.demonstrateNPlusOneProblem();
        return "Проблема N+1 отработала. Посмотри статистику (JDBC statements executed) в консоли IntelliJ IDEA!";
    }

    @GetMapping("/n-plus-one-solution")
    public String nPlusOneSolution() {
        labDemoService.demonstrateNPlusOneSolution();
        return "Решение N+1 отработало. Посмотри статистику (JDBC statements executed) в консоли IntelliJ IDEA!";
    }
}