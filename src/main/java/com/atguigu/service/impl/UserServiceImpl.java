package com.atguigu.service.impl;

import com.atguigu.dao.UserMapper;
import com.atguigu.pojo.User;
import com.atguigu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
//    @Resource
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /*
    * propagation传播行为: 7种
    *
    * 事务隔离级别:4种
    * 1. 未提交读   READ_UNCOMMIT
    * 2. 读已提交   READ_COMMIT     oracal 默认隔离级别
    * 4. 可重复读   REPEATABLE_READ MySQL默认隔离级别
    *
    * 问题: 数控丢失,脏读,不可重复读,幻读
    * */
    @Override
    @Transactional(readOnly = true,
            propagation = Propagation.SUPPORTS,
            isolation = Isolation.REPEATABLE_READ,
            rollbackFor = Exception.class,
            noRollbackFor = FileNotFoundException.class

    )//开启事务,并添加对象的属性配置
    public List<User> findAll() {
//        int i = 1/0; 运行时异常,Spring AOP声明事务,默认对于运行是异常进行回滚
//        FileNotFoundException xxx = new FileNotFoundException("XXX");//编译是异常,默认不回滚的

//        从缓存中查询数据,规定存储用户信息用string类型进行存储,存储的key就是userList
        List<User> userList = (List<User>) redisTemplate.boundValueOps("userkey").get();
//        如果缓存中没有数据,查询数据库,将查到的数据放入缓存
        if (userList==null){//缓存中没有
            userList = userMapper.selectAll();
            redisTemplate.boundValueOps("userkey").set(userList);
            System.out.println("从数据库中查询:userList = " + userList);
        }else {
            System.out.println("从redis缓存中查询:userList = " + userList);
        }
//        如果缓存中有数据,直接返回
        return userList;
    }
}
