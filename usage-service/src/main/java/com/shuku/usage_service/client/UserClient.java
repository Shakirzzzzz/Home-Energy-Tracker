package com.shuku.usage_service.client;

import com.shuku.usage_service.Dto.UserDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(url = "http://localhost:8080/api/v1/user")
public interface UserClient {

    @GetExchange("/{id}")
    UserDto getUserById(@PathVariable Long id);
}
