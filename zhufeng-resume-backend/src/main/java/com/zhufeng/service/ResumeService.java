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

    // 创建简历
    public ResumeVO createResume(CreateResumeDTO dto) {
        Long userId = UserContext.getUserId();
        
        Resume resume = new Resume();
        resume.setUserId(userId);
        resume.setTitle(dto.getTitle());
        resume.setTemplateId(dto.getTemplateId());
        resume.setResumeData(dto.getResumeData());
        resume.setStatus(1);  // 草稿状态
        
        resumeMapper.insert(resume);
        return convertToVO(resume);
    }

    // 获取用户的简历列表
    public List<ResumeListVO> getResumeList() {
        Long userId = UserContext.getUserId();
        List<Resume> resumes = resumeMapper.findByUserId(userId);
        return resumes.stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());
    }

    // 获取简历详情
    public ResumeVO getResumeById(Long id) {
        Long userId = UserContext.getUserId();
        Resume resume = resumeMapper.findById(id);
        
        if (resume == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "简历不存在");
        }
        
        // 校验是否是本人的简历
        if (!resume.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "无权访问该简历");
        }
        
        return convertToVO(resume);
    }

    // 更新简历
    public ResumeVO updateResume(Long id, UpdateResumeDTO dto) {
        Long userId = UserContext.getUserId();
        Resume resume = resumeMapper.findById(id);
        
        if (resume == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "简历不存在");
        }
        
        if (!resume.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "无权修改该简历");
        }
        
        // 更新字段
        if (dto.getTitle() != null) {
            resume.setTitle(dto.getTitle());
        }
        if (dto.getTemplateId() != null) {
            resume.setTemplateId(dto.getTemplateId());
        }
        if (dto.getResumeData() != null) {
            resume.setResumeData(dto.getResumeData());
        }
        if (dto.getStatus() != null) {
            resume.setStatus(dto.getStatus());
        }
        
        resumeMapper.update(resume);
        return convertToVO(resume);
    }

    // 删除简历
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

    // 转换方法
    private ResumeVO convertToVO(Resume resume) {
        ResumeVO vo = new ResumeVO();
        BeanUtils.copyProperties(resume, vo);
        return vo;
    }

    private ResumeListVO convertToListVO(Resume resume) {
        ResumeListVO vo = new ResumeListVO();
        BeanUtils.copyProperties(resume, vo);
        return vo;
    }
}