package com.lhiot.resource.service;

import com.lhiot.resource.domain.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 资源管理器 包括资源加载 注册 移除 等管理
 */
@Service
public class ResourceManagerService {

    @SuppressWarnings("unused")
    //private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public ResourceManagerService(){
    }

    public List<Resource> resourceList(){
        //todo 需要查询数据库资源
        List<Resource> resourceList=new ArrayList<>();

        resourceList.add(new Resource(1,"订单列表","https://food-see.com/order/add","查询订单列表"));
        resourceList.add(new Resource(1,"用户个人信息","http://localhost:9082/sns/userinfo","查询用户个人信息"));
        return resourceList;
    }
}
