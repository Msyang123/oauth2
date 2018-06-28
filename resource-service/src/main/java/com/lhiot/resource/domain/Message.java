package com.lhiot.resource.domain;

public enum Message {

    SUCCESS(0,"成功"),
    APPID_UNFIND(10000,"appid不存在"),
    CODE_UNVALID(10001,"code无效"),
    APP_SECRET_UNVALID(10002,"秘钥无效"),
    REDIRECT_URI_NOT_MATCH(10003, "redirect_uri域名与后台配置不一致"),
    ORDER_BARCODE_IDS_IS_BLANK(10004, "此公众号被封禁"),
    ACTIVITY_GWFL_REFUND_FAILURE(10005, "此公众号并没有这些scope的权限"),
    REFRESHTOKEN_UNVALID(10006,"RefreshToken无效"),
    ACCESSTOKEN_UNVALID(10007,"AccessToken无效"),

    RESOURCE_UNFIND(10201,"未找到资源"),
    RESOURCE_ACCESS_FORBIDDEN(10202,"资源禁止访问"),
    USER_UNFIND(20001,"用户不存在");

    private int code;
    private String value;

    Message(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public int getCode() {
        return code;
    }
}
