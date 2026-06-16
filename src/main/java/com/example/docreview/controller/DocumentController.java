package com.example.docreview.controller;

import com.example.docreview.entity.AuditAction;
import com.example.docreview.entity.Document;
import com.example.docreview.entity.DocumentStatus;
import com.example.docreview.entity.User;
import com.example.docreview.repository.DocumentRepository;
import com.example.docreview.repository.UserRepository;
import com.example.docreview.service.AuditLogService;
import com.example.docreview.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;


@Tag(name = "文件", description = "文件上傳、查詢、下載")
@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final AuditLogService auditLogService;

    public DocumentController(DocumentRepository documentRepository,
                              UserRepository userRepository,
                              FileStorageService fileStorageService,
                              AuditLogService auditLogService) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
        this.auditLogService = auditLogService;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) auth.getPrincipal();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));
    }

    // 1. 查詢當前登入者的文件
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

    // 3. 新增文件
    @PostMapping
    public ResponseEntity<Document> createDocument(@RequestBody Document document) {
        User currentUser = getCurrentUser();
        document.setUploader(currentUser);
        document.setStatus(DocumentStatus.PENDING);
        Document saved = documentRepository.save(document);
        auditLogService.log(saved, currentUser, AuditAction.UPLOAD, "手動新增文件");
        return ResponseEntity.ok(saved);
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

    // 5. 上傳文件
    @PostMapping("/upload")
    public ResponseEntity<Document> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "category", required = false) String category) {

        User currentUser = getCurrentUser();
        String filePath = fileStorageService.storeFile(file);

        Document document = new Document();
        document.setTitle(title);
        document.setDescription(description);
        document.setCategory(category);
        document.setFilePath(filePath);
        document.setFileName(file.getOriginalFilename());
        document.setFileSize(file.getSize());
        document.setUploader(currentUser);
        document.setStatus(DocumentStatus.PENDING);

        Document saved = documentRepository.save(document);
        auditLogService.log(saved, currentUser, AuditAction.UPLOAD, "上傳檔案：" + file.getOriginalFilename());
        return ResponseEntity.ok(saved);
    }

    // 6. 下載文件（只能下載自己的）
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        Document doc = documentRepository.findById(id)
                .filter(d -> d.getUploader().getId().equals(currentUser.getId()))
                .orElseThrow(() -> new RuntimeException("文件不存在或無權限"));

        try {
            Path filePath = Paths.get(doc.getFilePath()).toAbsolutePath().normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
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