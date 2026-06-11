package com.zhufeng.controller;

import com.zhufeng.common.Result;
import com.zhufeng.dto.AiGenerateDTO;
import com.zhufeng.dto.AiGrammarDTO;
import com.zhufeng.dto.AiPolishDTO;
import com.zhufeng.service.AiService;
import com.zhufeng.vo.AiGenerationVO;
import com.zhufeng.vo.AiGrammarVO;
import com.zhufeng.vo.AiPolishVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    // AI 语法检查
    @PostMapping("/grammar")
    public Result<AiGrammarVO> checkGrammar(@Valid @RequestBody AiGrammarDTO dto) {
        AiGrammarVO vo = aiService.checkGrammar(dto);
        return Result.success("语法检查完成", vo);
    }

    // AI 内容润色
    @PostMapping("/polish")
    public Result<AiPolishVO> polishContent(@Valid @RequestBody AiPolishDTO dto) {
        AiPolishVO vo = aiService.polishContent(dto);
        return Result.success("润色完成", vo);
    }

    // 获取可用AI模型列表
    @GetMapping("/models")
    public Result<List<Map<String, Object>>> getModels() {
        List<Map<String, Object>> models = aiService.getAvailableModels();
        return Result.success(models);
    }
}
