package com.zhufeng.service;

import com.zhufeng.common.BusinessException;
import com.zhufeng.common.ResultCode;
import com.zhufeng.common.UserContext;
import com.zhufeng.dto.CreateResumeDTO;
import com.zhufeng.dto.UpdateResumeDTO;
import com.zhufeng.entity.Resume;
import com.zhufeng.mapper.ResumeMapper;
import com.zhufeng.vo.ResumeListVO;
import com.zhufeng.vo.ResumeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResumeService {

    private final ResumeMapper resumeMapper;

    public ResumeService(ResumeMapper resumeMapper) {
        this.resumeMapper = resumeMapper;
    }

    /** 创建简历 */
    public ResumeVO createResume(CreateResumeDTO dto) {
        Long userId = UserContext.getUserId();

        Resume resume = new Resume();
        resume.setUserId(userId);
        resume.setTitle(dto.getTitle() != null ? dto.getTitle() : "新建简历");
        resume.setTemplateId(dto.getTemplateId() != null ? dto.getTemplateId() : "classic");
        resume.setBasic(dto.getBasic() != null ? dto.getBasic() : "{}");
        resume.setEducation("[]");
        resume.setExperience("[]");
        resume.setProjects("[]");
        resume.setSkillContent("");
        resume.setSelfEvaluationContent("");
        resume.setCertificates("[]");
        resume.setCustomData("{}");
        // 默认模块配置 — 使用 title 键（对齐前端 magic-resume 风格）
        resume.setMenuSections("[{\"id\":\"basic\",\"title\":\"基本信息\",\"icon\":\"👤\",\"enabled\":true,\"order\":0}," +
            "{\"id\":\"education\",\"title\":\"教育经历\",\"icon\":\"🎓\",\"enabled\":true,\"order\":1}," +
            "{\"id\":\"experience\",\"title\":\"工作经历\",\"icon\":\"💼\",\"enabled\":true,\"order\":2}," +
            "{\"id\":\"projects\",\"title\":\"项目经历\",\"icon\":\"🚀\",\"enabled\":true,\"order\":3}," +
            "{\"id\":\"skills\",\"title\":\"技能特长\",\"icon\":\"⚡\",\"enabled\":true,\"order\":4}," +
            "{\"id\":\"selfEvaluation\",\"title\":\"自我评价\",\"icon\":\"💬\",\"enabled\":true,\"order\":5}]");
        resume.setGlobalSettings(dto.getGlobalSettings() != null
            ? dto.getGlobalSettings()
            : "{\"themeColor\":\"#667eea\",\"fontFamily\":\"default\",\"baseFontSize\":14,\"headerSize\":18,\"subheaderSize\":16,\"lineHeight\":1.5,\"pagePadding\":40,\"sectionSpacing\":24,\"paragraphSpacing\":12}");
        resume.setStatus(1);
        resumeMapper.insert(resume);
        return convertToVO(resume);
    }

    /** 获取简历列表 */
    public List<ResumeListVO> getResumeList() {
        Long userId = UserContext.getUserId();
        List<Resume> resumes = resumeMapper.findByUserId(userId);
        return resumes.stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());
    }

    /** 获取简历详情 */
    public ResumeVO getResumeById(Long id) {
        Long userId = UserContext.getUserId();
        Resume resume = resumeMapper.findById(id);

        if (resume == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "简历不存在");
        }
        if (!resume.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "无权访问该简历");
        }
        return convertToVO(resume);
    }

    /** 更新简历 */
    public ResumeVO updateResume(Long id, UpdateResumeDTO dto) {
        Long userId = UserContext.getUserId();
        Resume resume = resumeMapper.findById(id);

        if (resume == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "简历不存在");
        }
        if (!resume.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "无权修改该简历");
        }

        if (dto.getTitle() != null) resume.setTitle(dto.getTitle());
        if (dto.getTemplateId() != null) resume.setTemplateId(dto.getTemplateId());
        if (dto.getBasic() != null) resume.setBasic(dto.getBasic());
        if (dto.getEducation() != null) resume.setEducation(dto.getEducation());
        if (dto.getExperience() != null) resume.setExperience(dto.getExperience());
        if (dto.getProjects() != null) resume.setProjects(dto.getProjects());
        if (dto.getSkillContent() != null) resume.setSkillContent(dto.getSkillContent());
        if (dto.getSelfEvaluationContent() != null) resume.setSelfEvaluationContent(dto.getSelfEvaluationContent());
        if (dto.getCertificates() != null) resume.setCertificates(dto.getCertificates());
        if (dto.getCustomData() != null) resume.setCustomData(dto.getCustomData());
        if (dto.getMenuSections() != null) resume.setMenuSections(dto.getMenuSections());
        if (dto.getGlobalSettings() != null) resume.setGlobalSettings(dto.getGlobalSettings());
        if (dto.getStatus() != null) resume.setStatus(dto.getStatus());

        resumeMapper.update(resume);
        return convertToVO(resume);
    }

    /** 删除简历 */
    public void deleteResume(Long id) {
        Long userId = UserContext.getUserId();
        Resume resume = resumeMapper.findById(id);

        if (resume == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "简历不存在");
        }
        if (!resume.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "无权删除该简历");
        }
        resumeMapper.deleteById(id);
    }

    /** 转换实体 → VO */
    private ResumeVO convertToVO(Resume resume) {
        ResumeVO vo = new ResumeVO();
        BeanUtils.copyProperties(resume, vo);
        return vo;
    }

    /** 转换实体 → 列表VO */
    private ResumeListVO convertToListVO(Resume resume) {
        ResumeListVO vo = new ResumeListVO();
        BeanUtils.copyProperties(resume, vo);
        return vo;
    }
}
