package com.example.docreview.entity;
/**
 * 文件審核狀態
 * 對應 documents 資料表的 status 欄位
 * 每份文件在生命週期中只會處於其中一種狀態
 *
 * 狀態流程：
 *
 *   上傳文件
 *      ↓
 *   PENDING（待審核）
 *      ↓
 *   ┌──────────────┐
 *   ↓              ↓
 * APPROVED      REJECTED
 * （核准）       （退回）
 */

public enum DocumentStatus {
    PENDING, APPROVED, REJECTED
}
//PENDING
    // 待審核：文件剛上傳，尚未有審核人員處理
    // 所有文件上傳後的預設狀態（在 Document.java 設定 = DocumentStatus.PENDING）
    // 此時 reviewer 欄位為 null，reviewedAt 欄位為 null


//APPROVED
    // 已核准：審核人員審核通過，文件正式生效
    // 此時 reviewer 欄位填入審核人員，reviewedAt 填入審核時間

//REJECTED
    // 已退回：審核人員認為文件有問題，退回給上傳者
    // 此時 AuditLog 的 comment 欄位應填寫退回原因
    // 上傳者可以修改後重新上傳（新增一筆文件，狀態重新從 PENDING 開始）