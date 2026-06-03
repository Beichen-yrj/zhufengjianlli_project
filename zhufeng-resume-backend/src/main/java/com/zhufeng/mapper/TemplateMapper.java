package com.zhufeng.mapper;

import com.zhufeng.entity.Template;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TemplateMapper {

    @Select("SELECT * FROM t_template WHERE status = 1 ORDER BY sort_order ASC")
    List<Template> findAll();

    @Select("SELECT * FROM t_template WHERE id = #{id}")
    Template findById(Long id);

    @Select("SELECT * FROM t_template WHERE status = 1 AND category = #{category} ORDER BY sort_order ASC")
    List<Template> findByCategory(String category);
}