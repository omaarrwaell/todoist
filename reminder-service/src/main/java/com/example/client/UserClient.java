package com.example.client;

import com.example.config.UserClientConfig;
import com.example.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user.service.url}",configuration = UserClientConfig.class)
public interface UserClient {
    @GetMapping("/api/users/{id}")
    UserDto getUser(@PathVariable("id") String id);
}