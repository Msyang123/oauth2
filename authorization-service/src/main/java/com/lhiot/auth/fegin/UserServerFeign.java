package com.lhiot.auth.fegin;

import com.lhiot.auth.domain.User;
import com.lhiot.auth.domain.ValidatePassword;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 验证码组件客户端
 * Created by yj on 6/27.
 */
@FeignClient(value = "user-service-v2-0", fallback = UserServerHystrix.class)
@Component
public interface UserServerFeign {

    /**
     * 查询仓库转换明细
     * @param validatePassword 是否验证密码
     * @param user 待验证用户
     * @return
     */
    @RequestMapping(value="/sns/validate/{validate_password}",method = RequestMethod.POST)
    ResponseEntity<User> validateUser(@PathVariable("validate_password") ValidatePassword validatePassword,
                                       @RequestBody User user);
}
