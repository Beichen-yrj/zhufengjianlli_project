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

    /**
     * 兼容旧代码：设置 AI 生成的简历 JSON 数据
     * 数据存入 basic 字段（JSONB 格式）
     */
    public void setResumeData(String resumeDataJson) {
        this.basic = resumeDataJson;
    }
}
