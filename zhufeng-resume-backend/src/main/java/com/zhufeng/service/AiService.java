package com.zhufeng.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zhufeng.common.UserContext;
import com.zhufeng.dto.AiGenerateDTO;
import com.zhufeng.dto.AiGrammarDTO;
import com.zhufeng.dto.AiPolishDTO;
import com.zhufeng.entity.Resume;
import com.zhufeng.mapper.ResumeMapper;
import com.zhufeng.vo.AiGenerationVO;
import com.zhufeng.vo.AiGrammarVO;
import com.zhufeng.vo.AiPolishVO;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AiService {

    private final ResumeMapper resumeMapper;

    public AiService(ResumeMapper resumeMapper) {
        this.resumeMapper = resumeMapper;
    }

    // ==================== AI 生成 ====================

    public AiGenerationVO generate(AiGenerateDTO dto) {
        Long userId = UserContext.getUserId();

        String generatedResumeData = generateMockResumeData(dto.getPrompt());

        Resume resume = new Resume();
        resume.setUserId(userId);
        resume.setTitle("AI 生成的简历");
        resume.setTemplateId(dto.getTemplateId());
        resume.setResumeData(generatedResumeData);
        resume.setStatus(1);
        resumeMapper.insert(resume);

        AiGenerationVO vo = new AiGenerationVO();
        vo.setResumeId(resume.getId());
        vo.setTitle(resume.getTitle());
        vo.setResumeData(generatedResumeData);

        return vo;
    }

    // ==================== AI 语法检查 ====================

    public AiGrammarVO checkGrammar(AiGrammarDTO dto) {
        String prompt = buildGrammarPrompt(dto.getContent());
        String apiResponse = callAiApi(dto.getEndpoint(), dto.getApiKey(), dto.getModelId(), prompt);
        return parseGrammarResult(apiResponse);
    }

    private String buildGrammarPrompt(String content) {
        return """
            你是中文简历语法检查专家。请检查以下简历内容中的错别字、标点错误和语法问题。

            对于每个错误，用 JSON 数组返回，格式：
            [{"position": 数字, "length": 数字, "text": "原文", "suggestion": "建议修改", "type": "typo"}]

            注意：
            - position 为错误文字在原文中的起始位置（从0开始计数）
            - type 为 "typo"（错别字）或 "punctuation"（标点错误）
            - 如果没有错误，返回空的 JSON 数组 []
            - 只返回 JSON 数组，不要其他任何文字

            原文：
            """ + content;
    }

    private AiGrammarVO parseGrammarResult(String response) {
        AiGrammarVO vo = new AiGrammarVO();
        List<AiGrammarVO.GrammarError> errors = new ArrayList<>();
        try {
            // 提取 JSON 数组部分
            String jsonStr = response.trim();
            int startIdx = jsonStr.indexOf('[');
            int endIdx = jsonStr.lastIndexOf(']');
            if (startIdx >= 0 && endIdx > startIdx) {
                jsonStr = jsonStr.substring(startIdx, endIdx + 1);
            }
            JSONArray arr = JSONUtil.parseArray(jsonStr);
            for (int i = 0; i < arr.size(); i++) {
                JSONObject item = arr.getJSONObject(i);
                AiGrammarVO.GrammarError err = new AiGrammarVO.GrammarError();
                err.setPosition(item.getInt("position", 0));
                err.setLength(item.getInt("length", 0));
                err.setText(item.getStr("text", ""));
                err.setSuggestion(item.getStr("suggestion", ""));
                err.setType(item.getStr("type", "typo"));
                errors.add(err);
            }
        } catch (Exception e) {
            // 解析失败时返回空列表
        }
        vo.setErrors(errors);
        return vo;
    }

    // ==================== AI 内容润色 ====================

    public AiPolishVO polishContent(AiPolishDTO dto) {
        String prompt = buildPolishPrompt(dto.getContent(), dto.getType());
        String polished = callAiApi(dto.getEndpoint(), dto.getApiKey(), dto.getModelId(), prompt);

        AiPolishVO vo = new AiPolishVO();
        vo.setOriginalContent(dto.getContent());
        vo.setPolishedContent(polished != null ? polished.trim() : dto.getContent());
        vo.setType(dto.getType());
        return vo;
    }

    private String buildPolishPrompt(String content, String type) {
        // 提取模式使用完全不同的 prompt
        if ("extract".equals(type)) {
            return """
                你是简历信息提取专家。请从以下文本中提取简历信息，只返回JSON对象，不要任何解释或额外文字。
                
                JSON格式（只返回有信息的字段，没有的字段不要返回）：
                {"name":"姓名","email":"邮箱","phone":"电话","gender":"性别","birthday":"出生日期","city":"城市","targetJob":"期望岗位","expectedSalary":"期望薪资","jobType":"全职/兼职/实习","skills":"技能用换行分隔","experience":[{"company":"公司","position":"职位","startDate":"开始","endDate":"结束","content":"工作内容"}],"projects":[{"name":"项目名","role":"角色","content":"项目内容"}],"education":[{"school":"学校","major":"专业","degree":"学历"}],"certificates":[{"name":"证书名","issuer":"颁发机构","date":"时间"}],"selfEvaluation":"自我评价"}
                
                待提取文本：
                %s
                """.formatted(content);
        }

        String typeHint = switch (type != null ? type : "general") {
            case "education" -> "这是教育经历，请优化描述使其更专业";
            case "experience" -> "这是工作经历，请使用 STAR 法则优化，量化成果";
            case "projects" -> "这是项目经历，请突出技术栈和成果";
            case "skills" -> "这是技能列表，请归类整理";
            case "selfEvaluation" -> "这是自我评价，请使其更真诚、更专业";
            case "basic" -> "这是基本信息，请检查格式";
            default -> "请优化以下内容使其更专业";
        };

        return """
            你是专业简历撰写顾问。%s

            要求：
            1. 使用 STAR 法则（情境、任务、行动、结果）优化经历描述
            2. 量化成果（如"提升30%%"、"管理5人团队"）
            3. 使用行业关键词
            4. 保持原有语言风格和真实性
            5. 直接返回优化后的完整文本，不要额外解释
            
            原文：
            %s
            """.formatted(typeHint, content);
    }

    // ==================== 获取模型列表 ====================

    public List<Map<String, Object>> getAvailableModels() {
        return List.of(
            Map.of("key", "deepseek", "name", "DeepSeek", "endpoint", "https://api.deepseek.com"),
            Map.of("key", "doubao", "name", "豆包", "endpoint", "https://ark.cn-beijing.volces.com/api/v3"),
            Map.of("key", "openai", "name", "OpenAI", "endpoint", "https://api.openai.com"),
            Map.of("key", "custom", "name", "自定义", "endpoint", "")
        );
    }

    // ==================== 通用 AI API 调用 ====================

    /**
     * 调用 OpenAI 兼容的 Chat Completions API
     */
    private String callAiApi(String endpoint, String apiKey, String modelId, String prompt) {
        try {
            String url = (endpoint != null && !endpoint.isBlank()
                ? endpoint : "https://api.openai.com") + "/v1/chat/completions";

            JSONObject body = JSONUtil.createObj();
            body.set("model", modelId != null ? modelId : "gpt-3.5-turbo");

            JSONArray messages = JSONUtil.createArray();
            messages.add(JSONUtil.createObj()
                .set("role", "user")
                .set("content", prompt));
            body.set("messages", messages);
            body.set("temperature", 0.3);

            HttpResponse response = HttpRequest.post(url)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(body.toString())
                .timeout(60000)
                .execute();

            if (response.getStatus() == 200) {
                JSONObject respJson = JSONUtil.parseObj(response.body());
                JSONArray choices = respJson.getJSONArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    JSONObject message = choices.getJSONObject(0).getJSONObject("message");
                    return message.getStr("content", "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ==================== 模拟数据 ====================

    private String generateMockResumeData(String prompt) {
        JSONObject data = JSONUtil.createObj();

        JSONObject basicInfo = JSONUtil.createObj();
        basicInfo.set("name", "张三");
        basicInfo.set("phone", "138-0000-0001");
        basicInfo.set("email", "zhangsan@example.com");
        basicInfo.set("jobIntention", "Java 后端开发工程师");
        data.set("basicInfo", basicInfo);

        data.set("education", JSONUtil.createArray().add(
            JSONUtil.createObj()
                .set("school", "XX大学")
                .set("major", "计算机科学与技术")
                .set("degree", "本科")
                .set("dateRange", new String[]{"2020-09", "2024-06"})
        ));

        data.set("experience", JSONUtil.createArray().add(
            JSONUtil.createObj()
                .set("company", "XX科技有限公司")
                .set("position", "Java 开发实习生")
                .set("dateRange", new String[]{"2023-06", "2023-12"})
                .set("description", "参与公司后端 API 开发，使用 SpringBoot + MyBatis 完成订单管理模块")
        ));

        data.set("projects", JSONUtil.createArray().add(
            JSONUtil.createObj()
                .set("name", "电商后台管理系统")
                .set("role", "后端开发")
                .set("description", "使用 SpringBoot + Vue3 + PostgreSQL 开发，实现商品管理、订单管理等功能")
        ));

        JSONArray skills = JSONUtil.createArray();
        skills.add("Java");
        skills.add("SpringBoot");
        skills.add("MyBatis");
        skills.add("PostgreSQL");
        skills.add("Redis");
        skills.add("Git");
        data.set("skills", skills);

        data.set("selfEvaluation", "具备扎实的 Java 基础和良好的编程习惯，有团队协作精神，热爱技术学习。");

        return data.toStringPretty();
    }
}
