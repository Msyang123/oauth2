package com.lhiot.auth.service;

import com.lhiot.auth.domain.*;
import com.lhiot.auth.util.JacksonUtils;
import com.lhiot.auth.util.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
public class AuthCodeCteater {

    private final RedisTemplate<String, String> redisTemplate;

    private final ThirdPartRegistService thirdPartRegistService;

    @Autowired
    public AuthCodeCteater(RedisTemplate<String, String> redisTemplate, ThirdPartRegistService thirdPartRegistService){
        this.redisTemplate = redisTemplate;
        this.thirdPartRegistService = thirdPartRegistService;
    }

    /**
     * code为临时授权码 5分钟内有效 且只能使用一次
     * appid	是	公众号的唯一标识
     redirect_uri	是	授权后重定向的回调链接地址， 请使用 urlEncode 对链接进行处理
     response_type	是	返回类型，请填写code
     scope	是	应用授权作用域，snsapi_base （不弹出授权页面，直接跳转，只能获取用户openid），
     snsapi_userinfo （弹出授权页面，可通过openid拿到昵称、性别、所在地。并且， 即使在未关注的情况下，只要用户授权，也能获取其信息 ）
     state	否	重定向后会带上state参数，开发者可以填写a-zA-Z0-9的参数值，最多128字节
     #wechat_redirect	是	无论直接打开还是做页面302重定向时候，必须带此参数
     username 用户账号 必须通过此信息来构造
     */
    public Code generateCode(GenerateCodeParam generateCodeParam,String username){
        //先去查询注册应用相关信息 验证appid 信息是否存在
        Code code=new Code();
        ThirdPartRegist thirdPartRegist= thirdPartRegistService.getByAppId(generateCodeParam.getAppid());
        if(thirdPartRegist==null){
            code.setMessage(Message.APPID_UNFIND);
            return code;
        }
        //通过一定算法产生code MD5

        //将code缓存redis 5分钟 5分钟内被使用就直接删除掉 如果没有使用五分钟后自动失效
        String salt= UUID.randomUUID().toString();

        String getCode=MD5.getMD5(generateCodeParam.toString()+username+salt);
        //将临时token保存到redis中
        redisTemplate.opsForValue().set(getCode,username,5, TimeUnit.MINUTES);
        code.setValue(getCode);
        return code;
    }

    /**
     * 第二步 获取access_token  第三方应用通过服务器接口获取
     * @param appid  第三方应用id
     * @param secret 第三方应用secret
     * @param code 第一步获取到的用户code
     * @param grantType  授权类型
     * @return
     */
    public AccessToken generateAccessToken(String appid,String secret,String code,String grantType){
        AccessToken accessToken=new AccessToken();
        ThirdPartRegist thirdPartRegist= thirdPartRegistService.getByAppId(appid);
        if(thirdPartRegist==null){
            accessToken.setMessage(Message.APPID_UNFIND);
            return accessToken;
        }else if(!thirdPartRegist.getSecret().equals(secret)){
            accessToken.setMessage(Message.APP_SECRET_UNVALID);
            return accessToken;
        }
        //获取redis中的code不存在或者失效
        String username=getUsernameByCode(code);
        if(username==null){
            accessToken.setMessage(Message.CODE_UNVALID);
            return accessToken;
        }
        //找到对应的临时授权码
        //产生指定的accessToken 产生方式 appid+username md5

        String accessTokenVal=MD5.getMD5(appid+username+UUID.randomUUID().toString());
        //产生指定的refreshToken 产生方式 accessTokenVal+username md5
        String refreshTokenVal=MD5.getMD5(accessTokenVal+username+UUID.randomUUID().toString());

        //openId计算 对于相同应用的相同用户一致 暂时只通过用户名+appid确定 要存库
        String openId=MD5.getMD5(username+appid);

        //获取当前授权用户信息
        accessToken.setAccessToken(accessTokenVal);

        accessToken.setRefreshToken(refreshTokenVal);

        accessToken.setOpenid(openId);

        //将信息放入redis中 需要做过渡处理token问题 顾此设置一小时05分钟超时
        redisTemplate.opsForValue().set(accessTokenVal,accessToken.toString(),65, TimeUnit.MINUTES);

        //refreshToken存储一个月
        redisTemplate.opsForValue().set(refreshTokenVal,accessToken.toString(),30, TimeUnit.DAYS);
        //移除掉code
        redisTemplate.delete(code);
        return accessToken;
    }

    public AccessToken generateAccessTokenByRefreshToken(String appid,String refreshToken) throws IOException {

        String accessTokenJson=redisTemplate.opsForValue().get(refreshToken);
        if(accessTokenJson==null){
            AccessToken returnAccessToken=new AccessToken();
            returnAccessToken.setMessage(Message.REFRESHTOKEN_UNVALID);
            return returnAccessToken;
        }
        AccessToken oldAccessToken = JacksonUtils.fromJson(accessTokenJson,AccessToken.class);

        //TODO 中间过度阶段需要做处理

        //移除原来的AccessToken
        redisTemplate.delete(oldAccessToken.getAccessToken());
        //重新产生oldAccessToken 返回
        //产生指定的accessToken 产生方式 appid+AccessToken md5
        String accessTokenVal=MD5.getMD5(appid+oldAccessToken.getAccessToken()+UUID.randomUUID().toString());
        oldAccessToken.setAccessToken(accessTokenVal);
        //将信息放入redis中 需要做过渡处理token问题 顾此设置一小时05分钟超时
        redisTemplate.opsForValue().set(accessTokenVal,oldAccessToken.toString(),65, TimeUnit.MINUTES);
        return oldAccessToken;
    }

    /**
     * 通过临时code获取用户名
     * @param codeVal
     * @return
     */
    private String getUsernameByCode(String codeVal){
        return redisTemplate.opsForValue().get(codeVal);
    }

}
