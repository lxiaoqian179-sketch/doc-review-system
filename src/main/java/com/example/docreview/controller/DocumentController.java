package com.example.docreview.controller;

import com.example.docreview.entity.Document;
import com.example.docreview.entity.DocumentStatus;
import com.example.docreview.entity.User;
import com.example.docreview.repository.DocumentRepository;
import com.example.docreview.repository.UserRepository;
import com.example.docreview.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.docreview.service.FileStorageService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    // 在 class 頂部加入注入
    private final FileStorageService fileStorageService;

    public DocumentController(DocumentRepository documentRepository,
                              UserRepository userRepository,
                              FileStorageService fileStorageService) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    // 取得當前登入使用者的工具方法
    // SecurityContextHolder 存著 JwtAuthenticationFilter 寫入的身份資訊
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) auth.getPrincipal();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));
    }

    // 1. 查詢所有文件（只回傳當前登入者上傳的文件）
    @GetMapping
    public List<Document> getMyDocuments() {
        User currentUser = getCurrentUser();
        return documentRepository.findByUploader(currentUser);
    }

    // 2. 查詢單一文件（只能查自己的）
    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocumentById(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        return documentRepository.findById(id)
                .filter(doc -> doc.getUploader().getId().equals(currentUser.getId()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. 新增文件（uploader 自動帶入當前登入者）
    @PostMapping
    public ResponseEntity<Document> createDocument(@RequestBody Document document) {
        User currentUser = getCurrentUser();
        document.setUploader(currentUser);
        document.setStatus(DocumentStatus.PENDING);
        return ResponseEntity.ok(documentRepository.save(document));
    }

    // 4. 刪除文件（只能刪自己的）
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        Document doc = documentRepository.findById(id)
                .filter(d -> d.getUploader().getId().equals(currentUser.getId()))
                .orElse(null);

        if (doc == null) {
            return ResponseEntity.notFound().build();
        }
        documentRepository.delete(doc);
        return ResponseEntity.noContent().build();
    }


    // 5. 上傳文件（multipart/form-data）
    @PostMapping("/upload")
    public ResponseEntity<Document> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "category", required = false) String category) {

        // 取得當前登入者
        User currentUser = getCurrentUser();

        // 儲存檔案，取得路徑
        String filePath = fileStorageService.storeFile(file);

        // 建立 Document 物件
        Document document = new Document();
        document.setTitle(title);
        document.setDescription(description);
        document.setCategory(category);
        document.setFilePath(filePath);
        document.setFileName(file.getOriginalFilename());
        document.setFileSize(file.getSize());
        document.setUploader(currentUser);
        document.setStatus(DocumentStatus.PENDING);

        return ResponseEntity.ok(documentRepository.save(document));
    }



    // 6. 下載文件（只能下載自己的）
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
        User currentUser = getCurrentUser();

        // 查詢文件，確認是自己的
        Document doc = documentRepository.findById(id)
                .filter(d -> d.getUploader().getId().equals(currentUser.getId()))
                .orElseThrow(() -> new RuntimeException("文件不存在或無權限"));

        try {
            // 從資料庫的 filePath 組合出完整路徑
            Path filePath = Paths.get(doc.getFilePath()).toAbsolutePath().normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // 設定回應 Header，讓瀏覽器知道這是下載檔案
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + doc.getFileName() + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            throw new RuntimeException("檔案路徑錯誤：" + e.getMessage());
        }
    }

    // 7. 查詢所有人的文件（只有 ADMIN 能用）
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }
}