package com.shuku.user_service.controller;

import com.shuku.user_service.dto.UserDto;
import com.shuku.user_service.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto){
        UserDto created = userService.createUser(userDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{Id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long Id){
        UserDto userDto = userService.getUserById(Id);
        if (userDto == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/{Id}")
    public ResponseEntity<String> updateUser(@PathVariable Long Id, @RequestBody UserDto userDto){
        try{
            userService.updateUser(Id,userDto);
            return ResponseEntity.ok("User Created Successfully");
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

        }

    }

    @DeleteMapping("/{Id}")
    public ResponseEntity<String> deleteById(@PathVariable Long Id){
        try{
            userService.deleteById(Id);
            return ResponseEntity.noContent().build();
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

