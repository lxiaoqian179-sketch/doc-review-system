package com.example.docreview.dto;

import com.example.docreview.entity.Document;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DocumentDTO {

    private Long id;
    private String title;
    private String description;
    private String fileName;
    private Long fileSize;
    private String category;
    private String status;       // PENDING / APPROVED / REJECTED
    private Long uploaderId;
    private String uploaderUsername;
    private Long reviewerId;
    private String reviewerUsername;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
    private String rejectReason;

    /**
     * 把 Document entity 轉成 DocumentDTO
     * 用途：Controller 不直接回傳 entity，避免洩漏不該給前端的欄位（如 file_path）
     */
    public static DocumentDTO from(Document doc) {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(doc.getId());
        dto.setTitle(doc.getTitle());
        dto.setDescription(doc.getDescription());
        dto.setFileName(doc.getFileName());
        dto.setFileSize(doc.getFileSize());
        dto.setCategory(doc.getCategory());
        dto.setStatus(doc.getStatus().name());
        dto.setCreatedAt(doc.getCreatedAt());
        dto.setReviewedAt(doc.getReviewedAt());
        dto.setRejectReason(doc.getRejectReason());

        if (doc.getUploader() != null) {
            dto.setUploaderId(doc.getUploader().getId());
            dto.setUploaderUsername(doc.getUploader().getUsername());
        }

        if (doc.getReviewer() != null) {
            dto.setReviewerId(doc.getReviewer().getId());
            dto.setReviewerUsername(doc.getReviewer().getUsername());
        }

        return dto;
    }
}