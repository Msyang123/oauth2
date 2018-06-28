package com.lhiot.auth.fegin;

import com.lhiot.auth.domain.User;
import com.lhiot.auth.domain.ValidatePassword;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 *
 * Created by yj on 6/27.
 * 熔断器
 */
@Slf4j
@Component
public class UserServerHystrix implements UserServerFeign {

	@Override
	public ResponseEntity<User> validateUser(ValidatePassword validatePassword,
									   User user){
		log.warn("Hystrix: validateUser - " + validatePassword+"-"+user);
		return null;
	}
}
