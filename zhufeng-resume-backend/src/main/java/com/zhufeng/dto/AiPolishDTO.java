package com.zhufeng.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiPolishDTO {
    @NotBlank(message = "模型类型不能为空")
    private String model;

    private String modelId;

    @NotBlank(message = "API Key不能为空")
    private String apiKey;

    private String endpoint;

    @NotBlank(message = "润色内容不能为空")
    private String content;

    /** 内容类型: basic/education/experience/project/skill/selfEvaluation */
    private String type;
}
