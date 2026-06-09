package com.zhufeng.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Resume {
    private Long id;
    private Long userId;
    private String title;
    private Long templateId;
    private String resumeData;  // JSONB 存为 String
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}