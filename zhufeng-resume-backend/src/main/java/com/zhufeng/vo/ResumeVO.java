package com.zhufeng.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ResumeVO {
    private Long id;
    private Long userId;
    private String title;
    private String templateId;
    private String templateName;

    // 各模块 JSON 字段，前端直接解析
    private String basic;
    private String education;
    private String experience;
    private String projects;
    private String skillContent;
    private String selfEvaluationContent;
    private String certificates;
    private String customData;
    private String menuSections;
    private String globalSettings;

    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
