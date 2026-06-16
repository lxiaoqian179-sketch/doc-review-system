package com.example.docreview.controller;

import com.example.docreview.entity.AuditLog;
import com.example.docreview.repository.AuditLogRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    public AuditLogController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    // 查詢所有審核紀錄（只有 ADMIN 能看）
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }

    // 查詢某份文件的操作紀錄
    @GetMapping("/document/{documentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AuditLog> getLogsByDocument(@PathVariable Long documentId) {
        return auditLogRepository.findByDocumentId(documentId);
    }
}