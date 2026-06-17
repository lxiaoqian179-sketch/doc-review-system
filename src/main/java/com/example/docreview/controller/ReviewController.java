package com.example.docreview.controller;

import com.example.docreview.dto.DocumentDTO;
import com.example.docreview.dto.ReviewRequest;
import com.example.docreview.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('REVIEWER', 'ADMIN')")
    public ResponseEntity<Page<DocumentDTO>> getPending(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reviewService.getPendingDocuments(page, size));
    }

    @PostMapping("/{docId}/approve")
    @PreAuthorize("hasAnyRole('REVIEWER', 'ADMIN')")
    public ResponseEntity<DocumentDTO> approve(
            @PathVariable Long docId,
            Principal principal) {
        return ResponseEntity.ok(reviewService.approve(docId, principal.getName()));
    }

    @PostMapping("/{docId}/reject")
    @PreAuthorize("hasAnyRole('REVIEWER', 'ADMIN')")
    public ResponseEntity<DocumentDTO> reject(
            @PathVariable Long docId,
            @RequestBody @Valid ReviewRequest request,
            Principal principal) {
        return ResponseEntity.ok(reviewService.reject(docId, request.getComment(), principal.getName()));
    }
}