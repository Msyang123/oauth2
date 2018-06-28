package com.lhiot.auth.domain;

import lombok.Data;

@Data
public class Code {

    //code作为换取access_token的票据，每次用户授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期

    private Message message=Message.SUCCESS;

    private String value;

    @Override
    public String toString() {
        return "{" +
                "\"message\":" + message +
                ", \"value\":\"" + value + "\"" +
                "}";
    }
}
