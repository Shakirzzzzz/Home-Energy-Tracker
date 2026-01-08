package com.shuku.user_service.service;

import com.shuku.user_service.dto.UserDto;
import com.shuku.user_service.entity.User;
import com.shuku.user_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public UserDto getUserById(Long Id){
        log.info("Getting user by id: {}",Id);
        Optional<User> optional = userRepository.findById(Id);
        if(optional.isPresent()){
            User user = optional.get();
            return toDto(user);
        }
        else{
            return null;
        }

    }

    public void updateUser(Long Id,UserDto userDto){
        log.info("Updating user with Id: {}",Id);
        User user = userRepository.findById(Id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setEmail(userDto.getEmail());
        user.setAddress(userDto.getAddress());
        user.setAlerting(userDto.isAlerting());
        user.setEnergyAlertingThreshold(userDto.getEnergyAlertingThreshold());
        userRepository.save(user);

    }

    public void deleteById(Long Id){
        log.info("Deleting user with Id: {}",Id);
        User user = userRepository.findById(Id).orElseThrow(()-> new IllegalArgumentException("User not found"));
        userRepository.delete(user);
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
