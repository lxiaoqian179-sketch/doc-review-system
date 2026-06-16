package com.example.docreview.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    // 儲存檔案到本機資料夾，回傳儲存路徑
    public String storeFile(MultipartFile file) {
        try {
            // 確保上傳資料夾存在，不存在就自動建立
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 用 UUID 產生唯一檔名，避免同名檔案覆蓋
            // 例如：a3f2c1d4-xxxx-xxxx-xxxx_report.pdf
            String originalFileName = file.getOriginalFilename();
            String uniqueFileName = UUID.randomUUID() + "_" + originalFileName;

            // 組合完整路徑並儲存
            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), filePath);

            return uploadDir + "/" + uniqueFileName;

        } catch (IOException e) {
            throw new RuntimeException("檔案儲存失敗：" + e.getMessage());
        }
    }
}