package com.lhiot.auth.domain;

import lombok.Data;

/**
 * 用户信息实体 应该迁移到用户中心体系中
 */
@Data
public class User {

    private String username;

    private String password;

    private String nick;

    private int age;

    private Message message=Message.SUCCESS;


    public User(){

    }
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String nick, int age) {
        this.username = username;
        this.nick = nick;
        this.age=age;
    }

    /**
     * 移除用户密码属性值 用于安全返回
     */
    public void removePasswordVal(){
        this.setPassword(null);
    }

    @Override
    public String toString() {
        return "{" +
                "\"username\":\"" + username + "\"" +
                ", \"password\":\"" + password + "\"" +
                ", \"message\": "+ message +
                "}";
    }
}
