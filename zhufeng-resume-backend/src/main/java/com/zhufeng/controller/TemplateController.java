package com.zhufeng.controller;

import com.zhufeng.common.Result;
import com.zhufeng.service.TemplateService;
import com.zhufeng.vo.TemplateVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/templates")
public class TemplateController {

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    // 获取所有模板
    @GetMapping
    public Result<List<TemplateVO>> getAllTemplates() {
        List<TemplateVO> templates = templateService.getAllTemplates();
        return Result.success(templates);
    }

    // 获取单个模板详情
    @GetMapping("/{id}")
    public Result<TemplateVO> getTemplateById(@PathVariable Long id) {
        TemplateVO template = templateService.getTemplateById(id);
        return Result.success(template);
    }

    // 按分类获取模板
    @GetMapping("/category/{category}")
    public Result<List<TemplateVO>> getTemplatesByCategory(@PathVariable String category) {
        List<TemplateVO> templates = templateService.getTemplatesByCategory(category);
        return Result.success(templates);
    }
}