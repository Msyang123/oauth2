package com.lhiot.resource.api;

import com.lhiot.resource.domain.Resource;
import com.lhiot.resource.service.ResourceManagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/resource")
@Api(description = "资源管理")
public class ResourceManagerApi {

    private final ResourceManagerService resourceManagerService;
    @Autowired
    public ResourceManagerApi(ResourceManagerService resourceManagerService){

        this.resourceManagerService = resourceManagerService;
    }

    @ApiOperation(value = "获取资源列表", notes = "获取用户资源列表")
    @GetMapping(value = "/list")
    public ResponseEntity<List<Resource>> getResourceList(){
        //加载维护的资源列表
        return ResponseEntity.ok(resourceManagerService.resourceList());
    }

}
