package com.zhufeng.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Resume {
    private Long id;
    private Long userId;
    private String title;
    private String templateId;

    // 各模块独立 JSONB 字段
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
    private String templateName;    // JOIN 查询时填充
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public void setResumeData(String generatedResumeData) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setResumeData'");
    }
}
