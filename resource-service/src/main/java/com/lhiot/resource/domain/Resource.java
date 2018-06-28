package com.lhiot.resource.domain;

import lombok.Data;

/**
 * 资源信息实体
 */
@Data
public class Resource {
    private int id;

    private String name;

    private String url;

    private String desc;

    private Message message=Message.SUCCESS;


    public Resource(){

    }

    public Resource(int id, String name, String url, String desc) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ",\"name\":\"" + name + "\"" +
                ", \"url\":\"" + url + "\"" +
                ", \"desc\":\"" + desc + "\"" +
                ", \"message\": "+ message +
                "}";
    }
}
