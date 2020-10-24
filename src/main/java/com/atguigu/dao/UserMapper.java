package com.atguigu.dao;

import com.atguigu.pojo.User;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

//通用Mapper,简化Mybatis开发
@Repository
public interface UserMapper extends Mapper<User> {
}
