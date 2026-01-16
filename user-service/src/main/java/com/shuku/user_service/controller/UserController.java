package com.shuku.user_service.controller;

import com.shuku.user_service.dto.UserDto;
import com.shuku.user_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User Actions Endpoints", description = "Operations related to user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Create new User", description = "Creates a new user in the database")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto){
        UserDto created = userService.createUser(userDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{Id}")
    @Operation(summary = "Fetch a user by Id", description = "Fetches a user from the database using userid")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long Id){
        UserDto userDto = userService.getUserById(Id);
        if (userDto == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/{Id}")
    @Operation(summary = "Update a user's details", description = "Make changes to a user stored in the database using the users userid")
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
    @Operation(summary = "Deletes a user", description = "Deletes a user from the database using the  userid")
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

