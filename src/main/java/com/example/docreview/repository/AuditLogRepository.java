package com.example.docreview.repository;

import com.example.docreview.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.docreview.entity.AuditAction;
import java.util.List;

/**
 * 審核紀錄 Repository
 * 負責 audit_logs 資料表的資料存取操作
 *
 * 繼承 JpaRepository<AuditLog, Long> 後，自動擁有以下方法，不需要寫任何程式碼：
 * - save(auditLog)        → 新增一筆審核紀錄（AuditLog 只新增，不修改）
 * - findAll()             → 查詢所有審核紀錄
 * - findById(id)          → 依 id 查詢單筆紀錄
 * - existsById(id)        → 確認紀錄是否存在
 * - count()               → 計算總筆數
 *
 * 未來可在這裡新增自訂查詢方法，例如：
 * - findByDocumentId(Long documentId)
 *   → 查詢某份文件的所有操作紀錄(物件)
 *
 * - findByOperatorId(Long operatorId)
 *   → 查詢某個使用者的所有操作紀錄(人)
 *
 * - findByAction(AuditAction action)
 *   → 依操作類型查詢（例如只查所有 REJECT 紀錄）(狀態)
 */
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    // 查詢某份文件的所有操作紀錄
    List<AuditLog> findByDocumentId(Long documentId);
    // 查詢某個使用者的所有操作紀錄
    List<AuditLog> findByOperatorId(Long operatorId);
}