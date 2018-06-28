package com.lhiot.auth.api;

import com.lhiot.auth.domain.AccessToken;
import com.lhiot.auth.service.AuthCodeCteater;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth2")
@Api(description = "第三方授权服务api")
public class AuthorizeManager {

    private final AuthCodeCteater authCodeCteater;
    @Autowired
    public AuthorizeManager(AuthCodeCteater authCodeCteater){
        this.authCodeCteater = authCodeCteater;
    }



    @ApiOperation(value = "第二步 通过code换取网页授权access_token", notes = "通过code换取网页授权access_token")
    @GetMapping(value = "/access_token")
    public ResponseEntity<AccessToken> accessToken(@RequestParam("appid")String appid,
                                        @RequestParam("secret")String secret,
                                            @RequestParam("code")String code,
                                            @RequestParam("grant_type")String grantType){


        //依据code 获取redis中的值
        return ResponseEntity.ok(authCodeCteater.generateAccessToken(appid,secret,code,grantType));
    }

    @ApiOperation(value = "第三步 通过RefreshToken换取网页授权access_token", notes = "通过RefreshToken换取网页授权access_token")
    @GetMapping(value = "/refresh_token")
    public ResponseEntity<AccessToken> refreshToken(@RequestParam("appid")String appid,
                                                   @RequestParam("refresh_token")String refreshToken,
                                                   @RequestParam("grant_type")String grantType) throws Exception{

        //依据code 获取redis中的值
        return ResponseEntity.ok(authCodeCteater.generateAccessTokenByRefreshToken(appid,refreshToken));
    }
}
