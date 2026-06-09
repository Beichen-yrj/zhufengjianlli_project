package com.zhufeng.dto;

import lombok.Data;

@Data
public class UpdateResumeDTO {
    private String title;
    private Long templateId;
    private String resumeData;
    private Integer status;
}