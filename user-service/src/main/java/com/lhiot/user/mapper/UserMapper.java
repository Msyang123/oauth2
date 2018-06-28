package com.lhiot.user.mapper;

import com.lhiot.user.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {


    int insertPaymentLog(User user);


    User getUser(User user);


    User getUserByname(User user);

    User findByOpenId(String openId);


}
