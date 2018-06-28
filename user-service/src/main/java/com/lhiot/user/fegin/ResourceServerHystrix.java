package com.lhiot.user.fegin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 *
 * Created by yj on 6/27.
 * 熔断器
 */
@Slf4j
@Component
public class ResourceServerHystrix implements ResourceServerFeign {

	@Override
	public ResponseEntity<List> getResourceList(){
		log.warn("Hystrix: getResourceList - ");
		return null;
	}
}
