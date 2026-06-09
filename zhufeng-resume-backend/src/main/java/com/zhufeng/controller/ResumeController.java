package com.zhufeng.controller;

import com.zhufeng.common.Result;
import com.zhufeng.dto.CreateResumeDTO;
import com.zhufeng.dto.UpdateResumeDTO;
import com.zhufeng.service.ResumeService;
import com.zhufeng.vo.ResumeListVO;
import com.zhufeng.vo.ResumeVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    // 创建简历
    @PostMapping
    public Result<ResumeVO> createResume(@Valid @RequestBody CreateResumeDTO dto) {
        ResumeVO resume = resumeService.createResume(dto);
        return Result.success(resume);
    }

    // 获取简历列表
    @GetMapping
    public Result<List<ResumeListVO>> getResumeList() {
        List<ResumeListVO> resumes = resumeService.getResumeList();
        return Result.success(resumes);
    }

    // 获取简历详情
    @GetMapping("/{id}")
    public Result<ResumeVO> getResumeById(@PathVariable Long id) {
        ResumeVO resume = resumeService.getResumeById(id);
        return Result.success(resume);
    }

    // 更新简历
    @PutMapping("/{id}")
    public Result<ResumeVO> updateResume(@PathVariable Long id, @RequestBody UpdateResumeDTO dto) {
        ResumeVO resume = resumeService.updateResume(id, dto);
        return Result.success(resume);
    }

    // 删除简历
    @DeleteMapping("/{id}")
    public Result<?> deleteResume(@PathVariable Long id) {
        resumeService.deleteResume(id);
        return Result.success("删除成功");
    }
}