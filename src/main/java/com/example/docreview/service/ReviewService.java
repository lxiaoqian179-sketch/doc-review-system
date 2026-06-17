package com.example.docreview.service;

import com.example.docreview.dto.DocumentDTO;
import com.example.docreview.entity.*;
import com.example.docreview.exception.ResourceNotFoundException;
import com.example.docreview.repository.AuditLogRepository;
import com.example.docreview.repository.DocumentRepository;
import com.example.docreview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final DocumentRepository documentRepo;
    private final UserRepository userRepo;
    private final AuditLogRepository auditLogRepo;

    public Page<DocumentDTO> getPendingDocuments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        return documentRepo.findByStatus(DocumentStatus.PENDING, pageable)
                .map(DocumentDTO::from);
    }

    @Transactional
    public DocumentDTO approve(Long docId, String reviewerUsername) {
        Document doc = getDocOrThrow(docId);
        User reviewer = getUserOrThrow(reviewerUsername);
        validatePending(doc);

        doc.setStatus(DocumentStatus.APPROVED);
        doc.setReviewer(reviewer);
        doc.setReviewedAt(LocalDateTime.now());
        documentRepo.save(doc);

        saveLog(doc, reviewer, AuditAction.APPROVE, null);
        return DocumentDTO.from(doc);
    }

    @Transactional
    public DocumentDTO reject(Long docId, String comment, String reviewerUsername) {
        Document doc = getDocOrThrow(docId);
        User reviewer = getUserOrThrow(reviewerUsername);
        validatePending(doc);

        doc.setStatus(DocumentStatus.REJECTED);
        doc.setReviewer(reviewer);
        doc.setReviewedAt(LocalDateTime.now());
        documentRepo.save(doc);

        saveLog(doc, reviewer, AuditAction.REJECT, comment);
        return DocumentDTO.from(doc);
    }

    private void validatePending(Document doc) {
        if (doc.getStatus() != DocumentStatus.PENDING) {
            throw new IllegalStateException(
                    "只有 PENDING 狀態可審核，目前狀態：" + doc.getStatus());
        }
    }

    private Document getDocOrThrow(Long id) {
        return documentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + id));
    }

    private User getUserOrThrow(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    private void saveLog(Document doc, User operator, AuditAction action, String comment) {
        auditLogRepo.save(AuditLog.builder()
                .document(doc).operator(operator)
                .action(action).comment(comment)
                .build());
    }
}