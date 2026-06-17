package com.example.docreview.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReviewRequest {
    @NotBlank(message = "退回原因不能為空")
    private String comment;
}