package com.example.docreview.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
/**
 * 審核紀錄表（audit_logs）
 * 記錄系統中每一個操作事件，只新增不修改，是完整的歷史紀錄
 * 對應開發計畫中「晁剛資訊：每次調閱都要留紀錄」的需求
 */

@Data// 告訴 Lombok 自動產生所有 getter / setter / toString / equals / hashCode
@Entity// 告訴 JPA：這個 class 對應資料庫的一張表
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "audit_logs")// 指定對應的資料表名稱為 audit_logs
public class AuditLog {

    @Id// 宣告這個欄位是主鍵
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // id 由資料庫自動遞增（1, 2, 3...），不需要自己填
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    // 多筆 AuditLog審核紀錄表 對應一份 Document（多對一關聯）
    // FetchType.LAZY：延遲載入，查詢 AuditLog 時不會自動把 Document 一起撈出來
    // 只有在程式實際存取 document 物件時，才會發出第二次 SQL 查詢
    @JoinColumn(name = "document_id", nullable = false)
    // 外鍵欄位名稱為 document_id，不可為 NULL（每筆紀錄一定要對應一份文件）
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    // 多筆 AuditLog 對應一個 User（多對一關聯）
    // 記錄「是誰」執行了這個操作
    @JoinColumn(name = "operator_id", nullable = false)
    // 外鍵欄位名稱為 operator_id，不可為 NULL（每筆紀錄一定要有操作者）
    private User operator;

    @Enumerated(EnumType.STRING)
    // 存入資料庫時存字串（"UPLOAD"、"APPROVE"...），而非數字索引
    // 用字串的好處：未來調整 Enum 順序也不會影響舊資料

    @Column(nullable = false)
    // 操作類型不可為 NULL，每筆紀錄一定要有明確的動作類型
    private AuditAction action;
    // 操作類型：UPLOAD / APPROVE / REJECT / VIEW（對應 AuditAction enum）

    @Column(columnDefinition = "TEXT")
    // columnDefinition = "TEXT"：指定 MySQL 欄位型別為 TEXT（可存大量文字）
    // 預設 String 對應 VARCHAR(255)，審核備註可能超過 255 字，所以改用 TEXT
    private String comment;
    // 備註說明，例如退回原因（可以是 null，因為不是每個操作都需要備註）


    @Column(name = "created_at")
    // 資料庫欄位名稱為 created_at，對應 Java 的駝峰命名 createdAt
    private LocalDateTime createdAt = LocalDateTime.now();
    // 操作時間，物件建立時自動帶入當下時間
    // 注意：AuditLog 只記錄建立時間，沒有 updatedAt（歷史紀錄不應該被修改）
}