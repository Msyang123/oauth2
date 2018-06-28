package com.lhiot.auth.api;

import com.lhiot.auth.domain.*;
import com.lhiot.auth.fegin.ResourceServerFeign;
import com.lhiot.auth.fegin.UserServerFeign;
import com.lhiot.auth.service.AuthCodeCteater;
import com.lhiot.auth.service.ThirdPartRegistService;
import com.lhiot.auth.util.JacksonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

@Controller
@RequestMapping("/oauth2")
@Api(description = "授权服务器登录鉴权页面,用于我们用户输入账号密码登录我们用户系统 账户登录信息不采用session(因为存在分布式共享问题)采用 redis解决存储问题")
//如何解决分布式情况下code值传递问题  现在cookie存储对应code的特征值 然后redis查询处理
public class AuthorizeLoginPanel {

    private final UserServerFeign userServerFeign;
    private final ResourceServerFeign resourceServerFeign;
    private static final String USERINFO = "userinfo",CODEPARAM="codeparam";


    private final AuthCodeCteater authCodeCteater;


    private final ThirdPartRegistService thirdPartRegistService;

    @Autowired
    public AuthorizeLoginPanel(UserServerFeign userServerFeign, ResourceServerFeign resourceServerFeign, AuthCodeCteater authCodeCteater, ThirdPartRegistService thirdPartRegistService) {
        this.userServerFeign = userServerFeign;
        this.resourceServerFeign = resourceServerFeign;
        this.authCodeCteater = authCodeCteater;
        this.thirdPartRegistService = thirdPartRegistService;
    }


    @ApiOperation(value = "第一步 获取Auth code\", notes = \"依据客户端应用请求 获取Auth code")
    @GetMapping(value = "/authorize")
    public void authorize(@RequestParam("appid") String appid,
                          @RequestParam("redirect_uri") String redirectUri,
                          @RequestParam("response_type") String responseType,
                          @RequestParam("scope") String scope,
                          @RequestParam("state") String state, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        GenerateCodeParam generateCodeParam = new GenerateCodeParam(appid, redirectUri, responseType, scope, state);
        //将所有请求参数写入到cookie中  5分钟内有效
        saveToCookie(CODEPARAM,generateCodeParam.toString(),2 * 60,request,response);
        //优先查询cookie中信息
        String username=getCookieVal(USERINFO,request);
        if(username!=null) {
            User findUser = new User();
            findUser.setUsername(username);
            //cookie中获取到的用户不需要验证密码
            ResponseEntity<User> result = userServerFeign.validateUser(ValidatePassword.NOT_VALIDATE,findUser);
            if (result != null&&result.getStatusCodeValue()<400) {
                String html;
                //调整到确认授权页 目前不采用静默授权 如果采用静默授权要收集用户对应用登录记录信息才做
                this.saveToCookie(USERINFO, username, 5 * 60, request, response);
                //确认登录后产生code
                Code code = authCodeCteater.generateCode(generateCodeParam,username);
                if (Message.SUCCESS.equals(code.getMessage())) {
                    html = confirmStr(generateCodeParam, code.getValue());
                } else {
                    //输出错误信息
                    html = code.toString();
                }
                PrintWriter out = response.getWriter();
                out.print(html);
                out.flush();
                out.close();
                return;
            }
        }
        //没有从cookie中获取信息 直接调整到登录页面去登录再授权
        PrintWriter out = response.getWriter();
        out.print(initLogStr());
        out.flush();
        out.close();
        return;
    }

    @ApiOperation(value = "第一步 获取Auth code 初始化登录", notes = "初始化登录")
    @GetMapping(value = "/initlogin")
    public void initlogin(HttpServletResponse response) throws IOException {

        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(initLogStr());
        out.flush();
        out.close();
    }

    @ApiOperation(value = "第一步 获取Auth code 输入账号密码登录", notes = "输入账号密码登录")
    @PostMapping(value = "/login")
    public void login(@RequestParam("username") String username, @RequestParam("password") String password,
                      HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setCharacterEncoding("UTF-8");


        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        ResponseEntity<User> result = userServerFeign.validateUser(ValidatePassword.VALIDATE,user);
        String html;
        if (result!=null&&result.getStatusCodeValue()<400&&result.getBody().getMessage().getCode() == 0) {
            this.saveToCookie(USERINFO, username, 5 * 60, request, response);
            //查询cookie中应用传递参数信息
            String codeparam=getCookieVal(CODEPARAM,request);
            //获取到应用传递参数信息
            GenerateCodeParam generateCodeParam=JacksonUtils.fromJson(codeparam,GenerateCodeParam.class);

            Code code=authCodeCteater.generateCode(generateCodeParam,username);
            if (Message.SUCCESS.equals(code.getMessage())) {
                html = confirmStr(generateCodeParam, code.getValue());
            } else {
                //输出错误信息
                html = code.toString();
            }
        } else {
            //未登录
            html = unloginStr();
        }
        PrintWriter out = response.getWriter();
        out.print(html);
        out.flush();
        out.close();
    }


    @ApiOperation(value = "第一步 获取Auth code 跳转链接到应用", notes = "初始化跳转链接到应用 给应用分配access_token")
    @GetMapping(value = "/redirect")
    public void redirect(@RequestParam("code") String code,@RequestParam("state") String state,@RequestParam("redirect_uri") String redirectUri,
                         HttpServletResponse response) throws IOException {

        StringBuffer html = new StringBuffer();
        //获取应用调整相关信息
        //TODO 渲染到页面 用于前端浏览器跳转应用返回
        response.setCharacterEncoding("UTF-8");

        redirectUri=redirectUri+"?code="+code+"&state="+state;

        html.append("<!DOCTYPE html>")
                .append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:th=\"http://www.thymeleaf.org\"")
                .append("xmlns:sec=\"http://www.thymeleaf.org/thymeleaf-extras-springsecurity3\">")
                .append("<head>")
                .append("<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />")

                .append("<script type=\"text/javascript\">")
                .append("window.location.href=\"")
                .append(redirectUri)
                .append("\";")
                .append("</script>")
                .append("</head>")
                .append("<body>")
                .append("</body>")
                .append("</html>");
        PrintWriter out = response.getWriter();
        out.print(html);
        out.flush();
        out.close();
    }

    private String confirmStr(GenerateCodeParam generateCodeParam, String codeVal){
        StringBuffer html = new StringBuffer();
        //查找第三方信息
        ThirdPartRegist thirdPartRegist = thirdPartRegistService.getByAppId(generateCodeParam.getAppid());

        ResponseEntity<List<Resource>>  resourceList= resourceServerFeign.getResourceList();
        StringBuffer resourceListShow=new StringBuffer("<br/><h1>允许访问您的权限:</h>");
        if(resourceList!=null&&resourceList.getStatusCodeValue()<400) {
            resourceListShow.append("<ul>");
            for (Resource resource : resourceList.getBody()) {
                resourceListShow.append("<li>");
                resourceListShow.append(resource.getName());
                resourceListShow.append("-");
                resourceListShow.append(resource.getDesc());
                resourceListShow.append("</li>");
            }
            resourceListShow.append("</ul>");
        }
        resourceListShow.append("");
        html.append("<!DOCTYPE html>")
                .append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:th=\"http://www.thymeleaf.org\" xmlns:sec=\"http://www.thymeleaf.org/thymeleaf-extras-springsecurity3\">")
                .append("<head>")
                .append("   <meta http-equiv='Content-Type' content='text/html; charset=utf-8' />")
                .append("</head>")
                .append("<body>")
                .append("  <h1>Welcome to login sgsl! 确认授权登录应用-")
                .append(thirdPartRegist.getAppName())
                .append("</h1>")
                .append(resourceListShow)
                .append(" <p>sure click <a href='/oauth2/redirect")
                .append("?code=")
                .append(codeVal)
                .append("&state=")
                .append(generateCodeParam.getState())
                .append("&redirect_uri=")
                .append(generateCodeParam.getRedirectUri())
                .append("'> here</a></p>")
                .append("</body>")
                .append("</html>");
        return html.toString();
    }

    private String initLogStr(){
        StringBuffer loginStr = new StringBuffer();
        loginStr.append("<!DOCTYPE html>")
                .append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:th=\"http://www.thymeleaf.org\"")
                .append("xmlns:sec=\"http://www.thymeleaf.org/thymeleaf-extras-springsecurity3\">")
                .append("<head>")
                .append("<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />")
                .append("</head>")
                .append("<body>")
                .append("<form action=\"/oauth2/login\" method=\"post\">")
                .append("<div><label> User Name : <input type=\"text\" name=\"username\"/> </label></div>")
                .append("<div><label> Password: <input type=\"password\" name=\"password\"/> </label></div>")
                .append("<div><input type=\"submit\" value=\"Sign In\"/></div>")
                .append("</form>")
                .append("</body>")
                .append("</html>");
        return loginStr.toString();
    }
    private String unloginStr() {
        StringBuffer html = new StringBuffer();
        //查找第三方信息
        html.append("<!DOCTYPE html>")
                .append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:th=\"http://www.thymeleaf.org\" xmlns:sec=\"http://www.thymeleaf.org/thymeleaf-extras-springsecurity3\">")
                .append("<head>")
                .append("   <meta http-equiv='Content-Type' content='text/html; charset=utf-8' />")
                .append("</head>")
                .append("<body>")
                .append("  <h1>未登录状态!</h1>")
                .append(" <p>Click <a href='/oauth2/initlogin")
                .append("'> here to login.</a></p>")
                .append("</body>")
                .append("</html>");
        return html.toString();
    }



    private void saveToCookie(String name, String value, int saveMaxAge, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        boolean isExist = false;
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {

            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    System.out.println("原值为:" + cookie.getValue());
                    cookie.setValue(URLEncoder.encode(value, "utf-8"));
                    cookie.setPath("/");
                    cookie.setMaxAge(saveMaxAge);// 单位为秒
                    System.out.println("被修改的cookie名字为:" + cookie.getName() + ",新值为:" + cookie.getValue());
                    response.addCookie(cookie);
                    isExist = true;
                    break;
                }
            }
        }
        //如果不存在cookie值 就写入
        if (!isExist) {
            Cookie cookie = new Cookie(name, URLEncoder.encode(value, "utf-8"));
            cookie.setMaxAge(saveMaxAge);// 单位为秒
            cookie.setPath("/");
            System.out.println("已添加===============");
            response.addCookie(cookie);
        }
    }
    private String getCookieVal(String cookieName,HttpServletRequest request) throws UnsupportedEncodingException {
        if(request.getCookies()==null)
            return null;
        for (Cookie c : request.getCookies()) {
            if (cookieName.equals(c.getName())) {
                return URLDecoder.decode(c.getValue(),"UTF-8");
            }
        }
        return null;
    }

}
