package com.shuku.user_service.service;

import com.shuku.user_service.dto.UserDto;
import com.shuku.user_service.entity.User;
import com.shuku.user_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {
    private UserRepository userRepository;


    public UserService(UserRepository userRepository){
        this.userRepository=userRepository;
    }
    public UserDto createUser(UserDto input){
        log.info("Creating user: {}",input);
        final User createdUser = User.builder()
                .name(input.getName())
                .surname(input.getSurname())
                .email(input.getEmail())
                .address(input.getAddress())
                .alerting(input.isAlerting())
                .energyAlertingThreshold(input.getEnergyAlertingThreshold())
                .build();
         final User saved = userRepository.save(createdUser);
         return toDto(saved);
    }

    private UserDto toDto(User user){
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .address(user.getAddress())
                .alerting(user.isAlerting())
                .build();

    }
}
