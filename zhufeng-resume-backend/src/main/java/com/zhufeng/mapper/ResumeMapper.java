package com.zhufeng.mapper;

import com.zhufeng.entity.Resume;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ResumeMapper {

    /** 根据用户ID查询所有简历 */
    @Select("SELECT id, user_id, title, template_id, basic, education, experience, projects, " +
            "skill_content, self_evaluation_content, certificates, custom_data, menu_sections, " +
            "global_settings, status, created_at, updated_at " +
            "FROM t_resume WHERE user_id = #{userId} ORDER BY updated_at DESC")
    @Results({
        @Result(property = "templateName", column = "template_id"),
    })
    List<Resume> findByUserId(Long userId);

    /** 根据ID查询单个简历 */
    @Select("SELECT id, user_id, title, template_id, basic, education, experience, projects, " +
            "skill_content, self_evaluation_content, certificates, custom_data, menu_sections, " +
            "global_settings, status, created_at, updated_at " +
            "FROM t_resume WHERE id = #{id}")
    @Results({
        @Result(property = "templateName", column = "template_id"),
    })
    Resume findById(Long id);

    /** 创建简历 */
    @Insert("INSERT INTO t_resume(user_id, title, template_id, basic, education, experience, " +
            "projects, skill_content, self_evaluation_content, certificates, custom_data, " +
            "menu_sections, global_settings, status) " +
            "VALUES(#{userId}, #{title}, #{templateId}, #{basic}::jsonb, #{education}::jsonb, " +
            "#{experience}::jsonb, #{projects}::jsonb, #{skillContent}, " +
            "#{selfEvaluationContent}, #{certificates}::jsonb, #{customData}::jsonb, " +
            "#{menuSections}::jsonb, #{globalSettings}::jsonb, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Resume resume);

    /** 更新简历 */
    @Update("UPDATE t_resume SET title = #{title}, template_id = #{templateId}, " +
            "basic = #{basic}::jsonb, education = #{education}::jsonb, " +
            "experience = #{experience}::jsonb, projects = #{projects}::jsonb, " +
            "skill_content = #{skillContent}, self_evaluation_content = #{selfEvaluationContent}, " +
            "certificates = #{certificates}::jsonb, custom_data = #{customData}::jsonb, " +
            "menu_sections = #{menuSections}::jsonb, global_settings = #{globalSettings}::jsonb, " +
            "status = #{status}, updated_at = CURRENT_TIMESTAMP " +
            "WHERE id = #{id}")
    void update(Resume resume);

    /** 删除简历 */
    @Delete("DELETE FROM t_resume WHERE id = #{id}")
    void deleteById(Long id);
}
