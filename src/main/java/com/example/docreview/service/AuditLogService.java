package com.example.docreview.service;

import com.example.docreview.entity.AuditAction;
import com.example.docreview.entity.AuditLog;
import com.example.docreview.entity.Document;
import com.example.docreview.entity.User;
import com.example.docreview.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    // 記錄一筆操作紀錄，在任何需要留紀錄的地方呼叫這個方法
    public void log(Document document, User operator, AuditAction action, String comment) {
        AuditLog auditLog = new AuditLog();
        auditLog.setDocument(document);
        auditLog.setOperator(operator);
        auditLog.setAction(action);
        auditLog.setComment(comment);
        auditLogRepository.save(auditLog);
    }
}