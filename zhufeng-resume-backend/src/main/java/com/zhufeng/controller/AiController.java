package com.zhufeng.controller;

import com.zhufeng.common.Result;
import com.zhufeng.dto.AiGenerateDTO;
import com.zhufeng.service.AiService;
import com.zhufeng.vo.AiGenerationVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    // AI 生成简历
    @PostMapping("/generate")
    public Result<AiGenerationVO> generate(@Valid @RequestBody AiGenerateDTO dto) {
        AiGenerationVO vo = aiService.generate(dto);
        return Result.success("AI 生成成功", vo);
    }
}