package com.zhufeng.mapper;

import com.zhufeng.entity.Resume;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ResumeMapper {

    // 根据用户ID查询所有简历
    @Select("SELECT r.*, t.name as template_name " +
            "FROM t_resume r " +
            "LEFT JOIN t_template t ON r.template_id = t.id " +
            "WHERE r.user_id = #{userId} " +
            "ORDER BY r.updated_at DESC")
    List<Resume> findByUserId(Long userId);

    // 根据ID查询
    @Select("SELECT r.*, t.name as template_name " +
            "FROM t_resume r " +
            "LEFT JOIN t_template t ON r.template_id = t.id " +
            "WHERE r.id = #{id}")
    Resume findById(Long id);

    // 创建简历（resume_data 加 ::jsonb 显式转换类型）
    @Insert("INSERT INTO t_resume(user_id, title, template_id, resume_data, status) " +
            "VALUES(#{userId}, #{title}, #{templateId}, #{resumeData}::jsonb, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Resume resume);

    // 更新简历
    @Update("UPDATE t_resume SET title = #{title}, template_id = #{templateId}, " +
            "resume_data = #{resumeData}::jsonb, status = #{status}, updated_at = CURRENT_TIMESTAMP " +
            "WHERE id = #{id}")
    void update(Resume resume);

    // 删除简历
    @Delete("DELETE FROM t_resume WHERE id = #{id}")
    void deleteById(Long id);
}