2018-6-28 日更新  

### oauth2  绿航(lhiot)实现解决方案
author:yijun

####初始化
创建数据库
   
1>执行数据库脚本
```
CREATE DATABASE `oauth2`
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) DEFAULT NULL,
  `password` varchar(64) DEFAULT NULL,
  `nick` varchar(64) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `open_id` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES ('1', 'user1', '111111', 'me', '2', 'asdasdfsdg');
INSERT INTO `t_user` VALUES ('2', 'user2', '123456', 'you', '6', 'asfsdg');

```   
2>修改yml配置 
user-service服务中 application.yml
```
  url: jdbc:mysql://[ip]:[port]/oauth2?useUnicode=true&characterEncoding=UTF-8&useSSL=false
  username: [username]
  password: [password]
  
  redis:
    host: [ip]
    port: [port]
    
    redis:
      host: [ip]
      port: [port]
      
  defaultZone: ${EUREKA_REPLICAS_LIST:http://[ip]:[port]/eureka}    
```
resource-service与authorization-service配置与之雷同

###功能模块
主题分三大模块 
authorization-service(授权服务)
resource-service(资源服务)
user-service(用户服务)

内部初始设置

初始用户账号1
user1
密码
111111

初始用户账号2
user2
密码
123456

初始第三方服务应用账号默认分配了三个账号

第三方服务appId：app1 第三方服务名称：客户app1 服务密码：1234

第三方服务appId：app2 第三方服务名称：客户app2 服务密码： 1235

第三方服务appId：app3 第三方服务名称：客户app3 服务密码： 1236

1>用户代理(浏览器)访问地址:

http://[ip]:9081/oauth2/authorize?appid=[appId]&redirect_uri=http%3a%2f%2fwww.food-see.com/ssxxx&response_type=code&scope=SCOPE&state=mydefaultsss
输入内部初始用户账号
验证通过后点击确认授权返回临时令牌(code)

2>服务器获取用户access_token地址：

http://[ip]:9081/oauth2/access_token?appid=[appId]&secret=[appSecret]&code=[code]&grant_type=authorization_code
注：此处每个code只能获取一次access_token 获取后自动失效 没有获取access_token 5分钟后失效
客户端自行保存用户access_token 有效时长2小时 refresh_token 30天

3>服务器刷新用户access_token地址：

http://localhost:9081/oauth2/refresh_token?appid=[appId]&refresh_token=[refresh_token]&grant_type=refresh_token
refresh_token为第二步客户端存储的refresh_token

###暂未实现功能
1>客户资源关联设置与控制

2>第三方应用开放平台设置后台管理系统

3>授权服务器与网关结合


协议参考：http://www.ruanyifeng.com/blog/2014/05/oauth_2_0.html