package com.example.docreview.repository;

import com.example.docreview.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * 文件 Repository
 * 負責 documents 資料表的資料存取操作
 *
 * 未來會在這裡新增的自訂查詢：
 * - findByUploaderId(Long uploaderId)
 *   → 查詢某個使用者上傳的所有文件
 *
 * - findByStatus(DocumentStatus status)
 *   → 依審核狀態查詢（例如查所有 PENDING 的文件）
 *
 * - findByTitleContaining(String keyword, Pageable pageable)
 *   → 關鍵字搜尋文件標題（對應意藍資訊的搜尋功能）
 *
 * - findByUploaderId(Long uploaderId, Pageable pageable)
 *   → 分頁查詢某使用者的文件列表
 */

public interface DocumentRepository extends JpaRepository<Document, Long> {
    // Week 4 實作文件上傳 API 時，會在這裡新增自訂查詢
}