package com.zhufeng.mapper;

import com.zhufeng.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM t_user WHERE id = #{id}")
    User findById(Long id);

    @Select("SELECT * FROM t_user WHERE username = #{username}")
    User findByUsername(String username);

    @Insert("INSERT INTO t_user(username, password, email, nickname) " +
            "VALUES(#{username}, #{password}, #{email}, #{nickname})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    @Update("UPDATE t_user SET email = #{email}, nickname = #{nickname}, " +
            "updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void update(User user);

    @Update("UPDATE t_user SET password = #{password}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void updatePassword(@Param("id") Long id, @Param("password") String password);
}