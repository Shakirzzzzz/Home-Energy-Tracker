package com.shuku.user_service.service;

import com.shuku.user_service.dto.UserDto;
import com.shuku.user_service.entity.User;
import com.shuku.user_service.exception.UserNotFoundException;
import com.shuku.user_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final JsonMapper jsonMapper;


    public UserService(UserRepository userRepository, JsonMapper jsonMapper){
        this.userRepository=userRepository;
        this.jsonMapper = jsonMapper;
    }


    public UserDto createUser(UserDto input){
        // log.info("Creating a user in the database.......");
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

    public UserDto getUserDtoById(Long Id){
        // log.info("Fetching user by id: {}", Id);
        Optional<User> optional = userRepository.findById(Id);
        if(optional.isPresent()){
            User user = optional.get();
            return toDto(user);
        }
        else{
            throw  new UserNotFoundException("User not found with id:" + Id);
        }

    }

    public void patchUser(Long Id, Map<String,Object> patchPayload){
        // log.info("partially upating user with id: {}", Id);
        Optional<User> optional = userRepository.findById(Id);
        if(optional.isEmpty()){
            throw new UserNotFoundException("User not found with id: "+ Id);
        }
        User user = optional.get();
        User updatedUser = jsonMapper.updateValue(user, patchPayload);
        User savedUser = userRepository.save(updatedUser);


    }

    public void updateUser(Long Id,UserDto userDto){
        //log.info("Updating the user with id: {}", Id);
        User user = userRepository.findById(Id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + Id));
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setEmail(userDto.getEmail());
        user.setAddress(userDto.getAddress());
        user.setAlerting(userDto.isAlerting());
        user.setEnergyAlertingThreshold(userDto.getEnergyAlertingThreshold());
        userRepository.save(user);

    }

    public void deleteById(Long Id){
        //log.info("Deleting a user with id: {}", Id);
        User user = userRepository.findById(Id).orElseThrow(()-> new UserNotFoundException("User not found with Id: "+ Id));
        userRepository.delete(user);
    }

    private UserDto toDto(User user){
        return UserDto.builder()
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .address(user.getAddress())
                .alerting(user.isAlerting())
                .energyAlertingThreshold(user.getEnergyAlertingThreshold())
                .build();

    }
}
