package com.example.docreview.entity;
//審核操作類型
//記錄使用者對文件執行的每一種動作，存入 audit_logs 資料表的 action 欄位

public enum AuditAction {
    UPLOAD, APPROVE, REJECT, VIEW
}
//UPLOAD,  // 上傳：使用者上傳一份新文件
//APPROVE, // 核准：審核人員核准這份文件
//REJECT,  // 退回：審核人員退回這份文件（通常需填寫備註說明原因）
//VIEW     // 查閱：使用者開啟查看這份文件的內容
