package com.zhufeng.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateResumeDTO {
    @NotBlank(message = "简历标题不能为空")
    private String title;
    
    private Long templateId;
    
    // 默认简历数据模板
    private String resumeData = "{\"basicInfo\":{},\"education\":[],\"experience\":[],\"projects\":[],\"skills\":[],\"selfEvaluation\":\"\"}";
}