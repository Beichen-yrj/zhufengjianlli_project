package com.zhufeng.dto;

import lombok.Data;

/**
 * 简历更新 DTO — 对应 magic-resume-main 数据模型
 * basic/education/experience/projects/skillContent/selfEvaluationContent 是 JSON 字符串
 */
@Data
public class UpdateResumeDTO {
    private String title;
    private String templateId;

    // 各模块独立 JSON 字段（与前端 Pinia store 对应）
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
}
