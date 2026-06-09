package com.zhufeng.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ResumeListVO {
    private Long id;
    private String title;
    private Long templateId;
    private String templateName;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}