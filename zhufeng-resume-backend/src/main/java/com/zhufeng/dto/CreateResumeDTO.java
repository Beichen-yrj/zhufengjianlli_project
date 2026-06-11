package com.zhufeng.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateResumeDTO {
    @NotBlank(message = "简历标题不能为空")
    private String title;

    /** 模板 ID */
    private String templateId;

    /** 初始基本信息 JSON */
    private String basic;

    /** 全局设置 JSON */
    private String globalSettings;
}
