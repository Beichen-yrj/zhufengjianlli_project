package com.zhufeng.vo;

import lombok.Data;

@Data
public class TemplateVO {
    private Long id;
    private String name;
    private String description;
    private String previewImage;
    private String templateConfig;
    private String category;
}