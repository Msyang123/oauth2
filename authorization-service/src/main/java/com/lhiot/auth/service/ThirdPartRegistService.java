package com.lhiot.auth.service;

import com.lhiot.auth.domain.ThirdPartRegist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 第三方注册应用相关信息
 * 以后要放到resource中管理
 */
@Service
public class ThirdPartRegistService {

    private List<ThirdPartRegist> thirdPartRegists;
    @Autowired
    public ThirdPartRegistService(){
        //TODO 此需要加载真实注册app信息
        thirdPartRegists=new ArrayList<ThirdPartRegist>(3);

        thirdPartRegists.add(new ThirdPartRegist("app1","客户app1","1234"));
        thirdPartRegists.add(new ThirdPartRegist("app2","客户app2","1235"));
        thirdPartRegists.add(new ThirdPartRegist("app3","客户app3","1236"));
    }

    public ThirdPartRegist getByAppId(String appId){
        for (ThirdPartRegist item:thirdPartRegists) {
            if(appId.equals(item.getAppId()))
            return item;
        }
        return null;
    }

}
