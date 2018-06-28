package com.lhiot.auth.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * appid	是	公众号的唯一标识
 redirect_uri	是	授权后重定向的回调链接地址， 请使用 urlEncode 对链接进行处理
 response_type	是	返回类型，请填写code
 scope	是	应用授权作用域，snsapi_base （不弹出授权页面，直接跳转，只能获取用户openid），
 snsapi_userinfo （弹出授权页面，可通过openid拿到昵称、性别、所在地。并且， 即使在未关注的情况下，只要用户授权，也能获取其信息 ）
 state	否	重定向后会带上state参数，开发者可以填写a-zA-Z0-9的参数值，最多128字节
 #wechat_redirect	是	无论直接打开还是做页面302重定向时候，必须带此参数
 */

@Data
public class GenerateCodeParam {

    public GenerateCodeParam() {
    }

    public GenerateCodeParam(String appid, String redirectUri, String responseType, String scope,  String state) {
        this.appid = appid;
        this.redirectUri = redirectUri;
        this.responseType = responseType;
        this.scope = scope;
        this.state = state;
    }

    private String appid;

    private String redirectUri;

    private String responseType;

    private String scope;


    private String state;


    @Override
    public String toString() {
        return "{" +
                "\"appid\":\"" + appid + "\"" +
                ", \"redirectUri\":\"" + redirectUri + "\"" +
                ", \"responseType\":\"" + responseType + "\"" +
                ", \"scope\":\"" + scope + "\"" +
                ", \"state\":\"" + state + "\"" +
                "}";
    }
}
