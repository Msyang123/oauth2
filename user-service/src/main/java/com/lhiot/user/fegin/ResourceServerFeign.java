package com.lhiot.user.fegin;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 验证码组件客户端
 * Created by yj on 6/27.
 */
@FeignClient(value = "resource-service-v2-0", fallback = ResourceServerHystrix.class)
@Component
public interface ResourceServerFeign {

    /**
     * 获取资源列表
     * @return
     */
    @RequestMapping(value="/resource/list",method = RequestMethod.GET)
    ResponseEntity<List> getResourceList();
}
