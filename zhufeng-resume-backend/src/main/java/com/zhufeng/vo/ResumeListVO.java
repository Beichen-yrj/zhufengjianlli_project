package com.zhufeng.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ResumeListVO {
    private Long id;
    private String title;
    private String templateId;
    private String templateName;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
