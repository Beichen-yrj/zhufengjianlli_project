package com.zhufeng.service;

import com.zhufeng.common.BusinessException;
import com.zhufeng.common.ResultCode;
import com.zhufeng.entity.Template;
import com.zhufeng.mapper.TemplateMapper;
import com.zhufeng.vo.TemplateVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TemplateService {

    private final TemplateMapper templateMapper;

    public TemplateService(TemplateMapper templateMapper) {
        this.templateMapper = templateMapper;
    }

    // 获取所有模板
    public List<TemplateVO> getAllTemplates() {
        List<Template> templates = templateMapper.findAll();
        return templates.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    // 获取模板详情
    public TemplateVO getTemplateById(Long id) {
        Template template = templateMapper.findById(id);
        if (template == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "模板不存在");
        }
        return convertToVO(template);
    }

    // 按分类获取模板
    public List<TemplateVO> getTemplatesByCategory(String category) {
        List<Template> templates = templateMapper.findByCategory(category);
        return templates.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    // 转换方法
    private TemplateVO convertToVO(Template template) {
        TemplateVO vo = new TemplateVO();
        BeanUtils.copyProperties(template, vo);
        return vo;
    }
}