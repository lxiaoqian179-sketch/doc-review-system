package com.example.docreview.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
/**
 * 文件表（documents）
 * 系統的核心資料表，每一筆代表一份被上傳的文件
 * 記錄文件的基本資訊、儲存路徑、審核狀態與相關人員
 */

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data// 告訴 Lombok 自動產生所有 getter / setter / toString / equals / hashCode
@Entity// 告訴 JPA：這個 class 對應資料庫的一張表
@Table(name = "documents")// 指定對應的資料表名稱為 documents
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 主鍵，由資料庫自動遞增
    private Long id;

    @Column(nullable = false, length = 200)
    // 不可為 NULL，對應 VARCHAR(200)
    private String title;
    // 文件標題，例如「2026年Q2財務報告」

    @Column(columnDefinition = "TEXT")
    // 用 TEXT 型別，允許存入較長的描述文字（超過 VARCHAR(255) 的限制）
    private String description;
    // 文件描述，補充說明文件內容（可以是 null，非必填）

    @Column(name = "file_path", nullable = false, length = 500)
    // 資料庫欄位名稱 file_path，對應 Java 駝峰命名 filePath
    // 不可為 NULL，路徑可能很長所以設 500
    private String filePath;
    // 檔案在伺服器上的儲存路徑，例如 "uploads/2026/06/report.pdf"
    // 注意：資料庫只存路徑字串，實際檔案存在伺服器的資料夾裡

    @Column(name = "file_name", nullable = false, length = 255)
    // 原始檔案名稱，不可為 NULL
    private String fileName;
    // 使用者上傳時的原始檔名，例如 "財務報告_最終版.pdf"
    // 和 filePath 分開存的原因：伺服器上的路徑可能會重新命名，但要保留原始檔名給使用者看

    @Column(name = "file_size")
    // 可以是 null（允許不記錄檔案大小）
    private Long fileSize;
    // 檔案大小，單位是 bytes（位元組）
    // 例如：1MB = 1,048,576 bytes，用 Long 確保不會溢位

    @Column(length = 50)
    // 對應 VARCHAR(50)，可以是 null（未分類）
    private String category;
    // 文件分類，例如「合約」、「報告」、「申請單」
    // 未來可改成 Enum，目前用字串保持彈性

    @Enumerated(EnumType.STRING)
    // 存字串而非數字索引，避免 Enum 順序變動時舊資料對應錯誤
    @Column(nullable = false)
    // 審核狀態不可為 NULL
    private DocumentStatus status = DocumentStatus.PENDING;
    // 審核狀態，預設為 PENDING（待審核）
    // 狀態流程：PENDING → APPROVED（核准）或 REJECTED（退回）

    @ManyToOne(fetch = FetchType.LAZY)
    // 多份文件對應一個上傳者（多對一）
    // LAZY：不會在查詢 Document 時自動把 User 資料一起撈出來
    @JoinColumn(name = "uploader_id", nullable = false)
    // 外鍵欄位 uploader_id，不可為 NULL（文件一定有上傳者）
    private User uploader;
    // 上傳這份文件的使用者

    @ManyToOne(fetch = FetchType.LAZY)
    // 多份文件對應一個審核者（多對一）
    @JoinColumn(name = "reviewer_id")
    // 外鍵欄位 reviewer_id，可以是 NULL
    // 原因：文件剛上傳時還沒有人審核，reviewer 尚未指定
    private User reviewer;
    // 審核這份文件的人員（上傳時為 null，審核後才填入）

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    // 文件上傳時間，物件建立時自動帶入當下時間

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
    // 審核完成時間，預設為 null
    // 審核人員執行核准或退回時，才手動設定這個時間
    // 和 createdAt 不同：不能給預設值，因為不是每份文件都已審核


    @Column(name = "reject_reason", columnDefinition = "TEXT")
    private String rejectReason;
// 最新一次退回原因（若曾被退回），核准後可選擇是否清空，目前保留歷史值



}