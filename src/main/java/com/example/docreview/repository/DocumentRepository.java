package com.example.docreview.repository;

import com.example.docreview.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.docreview.entity.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.docreview.entity.DocumentStatus; // 確認你的 enum 路徑
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

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
    List<Document> findByUploader(User uploader);
    Page<Document> findByStatus(DocumentStatus status, Pageable pageable);
    Page<Document> findByTitleContainingOrDescriptionContainingOrCategoryContaining(
            String titleKeyword, String descKeyword, String categoryKeyword, Pageable pageable);
    long countByStatus(DocumentStatus status);//計算文件各狀態的數量
    @Query("SELECT d.title FROM Document d")
    List<String> findAllTitles();// 加一個只抓 title 的方法
    @Query(value = "SELECT DATE(created_at) as date, COUNT(*) as count " +
            "FROM documents " +
            "WHERE created_at >= :startDate " +
            "GROUP BY DATE(created_at) " +
            "ORDER BY date ASC",
            nativeQuery = true)
    List<Object[]> findUploadTrend(@Param("startDate") LocalDateTime startDate);
    //trend（近 30 天上傳/審核趨勢）
}