package com.zhufeng.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Template {
    private Long id;
    private String name;
    private String description;
    private String previewImage;
    private String templateConfig;  // JSONB 存为 String，MyBatis 会自动转换
    private String category;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createdAt;
}