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

    private final LabDemoService demoService;

    // Тест 1: Без транзакции (Демонстрация проблемы)
    @GetMapping("/no-tx")
    public String testWithoutTransaction() {
        try {
            demoService.saveDataWithoutTransaction();
        } catch (Exception e) {
            return "ОШИБКА ПОЙМАНА! <br> Зайди в pgAdmin в таблицу 'users'. " +
                    "Пользователь 'hacker_no_tx' сохранился, хотя процесс прервался ошибкой. " +
                    "База данных загрязнена частичными данными.";
        }
        return "Успех";
    }

    // Тест 2: С транзакцией (Правильное решение)
    @GetMapping("/with-tx")
    public String testWithTransaction() {
        try {
            demoService.saveDataWithTransaction();
        } catch (Exception e) {
            return "ОШИБКА ПОЙМАНА! <br> Зайди в pgAdmin в таблицу 'users'. " +
                    "Пользователя 'hacker_with_tx' ТАМ НЕТ. " +
                    "@Transactional успешно отменил (ROLLBACK) операцию сохранения юзера из-за ошибки!";
        }
        return "Успех";
    }
}