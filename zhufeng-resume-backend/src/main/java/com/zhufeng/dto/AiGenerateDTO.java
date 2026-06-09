package com.zhufeng.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiGenerateDTO {
    @NotBlank(message = "提示词不能为空")
    private String prompt;
    
    private Long templateId;  // 可选，指定模板
}