package com.example.messenger.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private static final String UPLOAD_DIR = "uploads/";

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String originalFilename = file.getOriginalFilename();
            String cleanFilename = originalFilename != null ? StringUtils.cleanPath(originalFilename) : "unknown";
            String fileName = UUID.randomUUID() + "_" + cleanFilename;


            Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName).normalize();

            Files.write(filePath, file.getBytes());

            return ResponseEntity.ok("/uploads/" + fileName);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Ошибка загрузки файла");
        }
    }
}