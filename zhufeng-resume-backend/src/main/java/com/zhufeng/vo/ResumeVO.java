package com.zhufeng.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ResumeVO {
    private Long id;
    private Long userId;
    private String title;
    private Long templateId;
    private String templateName;  // 模板名称（额外字段）
    private String resumeData;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}