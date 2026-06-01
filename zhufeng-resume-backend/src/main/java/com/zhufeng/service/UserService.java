package com.zhufeng.service;

import com.zhufeng.common.BusinessException;
import com.zhufeng.common.ResultCode;
import com.zhufeng.common.UserContext;
import com.zhufeng.entity.User;
import com.zhufeng.mapper.UserMapper;
import com.zhufeng.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    // 获取当前登录用户的信息
    public UserVO getCurrentUser() {
        // 第1步：从 UserContext 获取当前用户的 ID
        // UserContext 的数据是拦截器存进来的
        Long userId = UserContext.getUserId();
        
        // 第2步：检查用户 ID 是否存在（未登录情况）
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        
        // 第3步：根据用户 ID 查询数据库
        User user = userMapper.findById(userId);
        
        // 第4步：检查用户是否存在（可能被删除了）
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "用户不存在");
        }
        
        // 第5步：转换数据，复制属性（不复制密码）
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        
        return userVO;
    }
}