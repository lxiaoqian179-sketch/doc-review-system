package com.example.docreview.controller;

import com.example.docreview.entity.DocumentStatus;
import com.example.docreview.repository.DocumentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final DocumentRepository documentRepository;

    public StatsController(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> overview() {
        Map<String, Long> result = new HashMap<>();
        result.put("pending", documentRepository.countByStatus(DocumentStatus.PENDING));
        result.put("approved", documentRepository.countByStatus(DocumentStatus.APPROVED));
        result.put("rejected", documentRepository.countByStatus(DocumentStatus.REJECTED));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/keywords")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> keywords() {
        List<String> titles = documentRepository.findAllTitles();

        // 排除常見無意義字（語助詞、單位詞等）
        Set<Character> stopWords = Set.of(
                '的', '是', '了', '在', '與', '及', '和', '份', '第', '這', '那', '個'
        );

        Map<String, Long> wordCount = new HashMap<>();
        for (String title : titles) {
            if (title == null) continue;
            for (char c : title.toCharArray()) {
                if (Character.isWhitespace(c)) continue;
                if (stopWords.contains(c)) continue;
                String word = String.valueOf(c);
                wordCount.merge(word, 1L, Long::sum);
            }
        }

        List<Map<String, Object>> result = wordCount.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(20)
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("word", entry.getKey());
                    item.put("count", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/trend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> trend() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        List<Object[]> rawResult = documentRepository.findUploadTrend(startDate);

        List<Map<String, Object>> result = rawResult.stream()
                .map(row -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("date", row[0].toString());
                    item.put("count", ((Number) row[1]).longValue());
                    return item;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}