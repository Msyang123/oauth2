package com.lhiot.auth.domain;

import lombok.Data;

/**
 * 第三方注册应用信息实体
 */
@Data
public class ThirdPartRegist {

    private String appId;

    private String appName;

    private String secret;

    private String ownUrl;//应用url

    public ThirdPartRegist(){

    }
    public ThirdPartRegist(String appId, String appName, String secret) {
        this.appId = appId;
        this.appName = appName;
        this.secret = secret;
    }
}
