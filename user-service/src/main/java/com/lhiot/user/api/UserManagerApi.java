package com.lhiot.user.api;

import com.lhiot.user.domain.AccessToken;
import com.lhiot.user.domain.Message;
import com.lhiot.user.domain.User;
import com.lhiot.user.domain.ValidatePassword;
import com.lhiot.user.service.UserManagerService;
import com.lhiot.user.util.JacksonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/sns")
//TODO 应该迁移到用户中心体系中
@Api(description = "用户管理")
public class UserManagerApi {

    /**
     * 先模拟产生用户信息 实际应该从用户中心获取
     * @param redisTemplate
     */
    private final RedisTemplate<String, String> redisTemplate;
    private final UserManagerService userManagerService;
    @Autowired
    public UserManagerApi(RedisTemplate<String, String> redisTemplate, UserManagerService userManagerService){
        //实际会将用户的信息划分不同应用用户
        this.redisTemplate = redisTemplate;
        this.userManagerService = userManagerService;
    }

    @ApiOperation(value = "拉取用户信息", notes = "拉取用户信息")
    @GetMapping(value = "/userinfo")
    public ResponseEntity sendVerifycode(@RequestParam("access_token") String accessToken,
                                         @RequestParam("openid") String openid) throws IOException {
        //TODO 通过远程拉取认证服务器获取token信息 认证accessToken是否有效
        AccessToken accessTokenFromRedis= JacksonUtils.fromJson(redisTemplate.opsForValue().get(accessToken),AccessToken.class);
        AccessToken resultAccessToken=new AccessToken();
        if (accessTokenFromRedis==null){

            resultAccessToken.setMessage(Message.ACCESSTOKEN_UNVALID);
            return ResponseEntity.badRequest().body(resultAccessToken);
        }
        User user=userManagerService.findByOpenId(openid);
        if(Objects.nonNull(user)) {
            return ResponseEntity.ok(user);
        }
        resultAccessToken.setMessage(Message.USER_UNFIND);
        return ResponseEntity.badRequest().body(resultAccessToken);
    }

    @ApiOperation(value = "验证用户信息", notes = "验证用户信息")
    @PostMapping(value = "/validate/{validate_password}")
    public ResponseEntity<User> validate(@PathVariable("validate_password") ValidatePassword validatePassword,
                                         @RequestBody User user){
        User resultUser=userManagerService.validate(user,validatePassword);
        if(Objects.isNull(resultUser)){
            resultUser=new User();
            resultUser.setMessage(Message.USER_UNFIND);
            return ResponseEntity.badRequest().body(resultUser);
        }
        return ResponseEntity.ok(resultUser);
    }
}
