package com.example.service.impl;

import com.example.mapper.UserMapper;
import com.example.model.User;
import com.example.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
public class UserServiceImpl implements UserService {

    @Resource
    UserMapper userMapper;

    @Override
    public List<User> getList() {
        int i = 1 / 0;
        return userMapper.getList();
    }

    @Override
    public void update(User user) {
        userMapper.update(user);
        throw new RuntimeException("运行时易出错");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(User user) {
        userMapper.delete(user);
        // 构造一个ConvertException()，看是否会回滚事务
//        throw new ConvertException("转换异常");
    }


}
