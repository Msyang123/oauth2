package com.lhiot.user.service;

import com.lhiot.user.domain.Message;
import com.lhiot.user.domain.User;
import com.lhiot.user.domain.ValidatePassword;
import com.lhiot.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 用户服务
 */
@Service
public class UserManagerService {

    private final RedisTemplate<String, String> redisTemplate;

    private final UserMapper userMapper;

    @Autowired
    public UserManagerService(RedisTemplate<String, String> redisTemplate,  UserMapper userMapper){
        this.redisTemplate = redisTemplate;
        this.userMapper = userMapper;
    }

    public User findByOpenId(String openId){
        return userMapper.findByOpenId(openId);
    }
    /**
     * 验证用户是否存在
     * 将返回的信息写入cookie中 并且存入redis中用户sessin缓存
     * @param user
     * @param validatePassword 是否验证密码
     * @return
     */
    public User validate(User user, ValidatePassword validatePassword){
        User result=new User();
        result.setMessage(Message.USER_UNFIND);
        if(user==null)
            return result;
        if (validatePassword.equals(ValidatePassword.VALIDATE)){
            result=userMapper.getUser(user);
        }else {
            result=userMapper.getUserByname(user);
        }
        return result;
    }

}
