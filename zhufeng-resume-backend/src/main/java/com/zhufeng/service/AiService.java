package com.zhufeng.service;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zhufeng.common.UserContext;
import com.zhufeng.dto.AiGenerateDTO;
import com.zhufeng.entity.Resume;
import com.zhufeng.mapper.ResumeMapper;
import com.zhufeng.vo.AiGenerationVO;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    private final ResumeMapper resumeMapper;

    // AI API 配置
    private static final String AI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String AI_API_KEY = "your-api-key-here"; // TODO: 替换为真实Key

    public AiService(ResumeMapper resumeMapper) {
        this.resumeMapper = resumeMapper;
    }

    public AiGenerationVO generate(AiGenerateDTO dto) {
        Long userId = UserContext.getUserId();

        // TODO: 接入真实 AI 接口，这里先用模拟数据演示流程
        String generatedResumeData = generateMockResumeData(dto.getPrompt());

        // 创建简历记录
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

    // 模拟 AI 生成数据
    private String generateMockResumeData(String prompt) {
        JSONObject data = JSONUtil.createObj();
        
        // 基本信息
        JSONObject basicInfo = JSONUtil.createObj();
        basicInfo.set("name", "张三");
        basicInfo.set("phone", "138-0000-0001");
        basicInfo.set("email", "zhangsan@example.com");
        basicInfo.set("jobIntention", "Java 后端开发工程师");
        data.set("basicInfo", basicInfo);

        // 教育经历
        data.set("education", JSONUtil.createArray().add(
            JSONUtil.createObj()
                .set("school", "XX大学")
                .set("major", "计算机科学与技术")
                .set("degree", "本科")
                .set("dateRange", new String[]{"2020-09", "2024-06"})
        ));

        // 工作经历
        data.set("experience", JSONUtil.createArray().add(
            JSONUtil.createObj()
                .set("company", "XX科技有限公司")
                .set("position", "Java 开发实习生")
                .set("dateRange", new String[]{"2023-06", "2023-12"})
                .set("description", "参与公司后端 API 开发，使用 SpringBoot + MyBatis 完成订单管理模块")
        ));

        // 项目经历
        data.set("projects", JSONUtil.createArray().add(
            JSONUtil.createObj()
                .set("name", "电商后台管理系统")
                .set("role", "后端开发")
                .set("description", "使用 SpringBoot + Vue3 + PostgreSQL 开发，实现商品管理、订单管理等功能")
        ));

                // 技能
        JSONArray skills = JSONUtil.createArray();
        skills.add("Java");
        skills.add("SpringBoot");
        skills.add("MyBatis");
        skills.add("PostgreSQL");
        skills.add("Redis");
        skills.add("Git");
        data.set("skills", skills);

        // 自我评价
        data.set("selfEvaluation", "具备扎实的 Java 基础和良好的编程习惯，有团队协作精神，热爱技术学习。");

        return data.toStringPretty();
    }
}