package com.shuku.user_service.service;

import com.shuku.user_service.config.KeycloakConfiguration;
import com.shuku.user_service.dto.UserDto;
import com.shuku.user_service.entity.User;
import com.shuku.user_service.exception.UserNotFoundException;
import com.shuku.user_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final JsonMapper jsonMapper;
    private final KeycloakConfiguration keyCloakConfiguration;
    private final Keycloak keycloakClient;


    public UserService(UserRepository userRepository, JsonMapper jsonMapper, Keycloak keycloakClient, KeycloakConfiguration keyCloakConfiguration){
        this.userRepository=userRepository;
        this.jsonMapper = jsonMapper;
        this.keycloakClient = keycloakClient;
        this.keyCloakConfiguration = keyCloakConfiguration;
    }


    public UserDto createUser(UserDto input){
        System.out.println("+======== Entered createUser==========+");
        RealmResource realmResource = keycloakClient.realm(keyCloakConfiguration.realm());
        UserRepresentation user = new UserRepresentation();
        user.setFirstName(input.getName());
        user.setLastName(input.getSurname());
        user.setEmail(input.getEmail());
        user.setUsername(input.getUsername());
        user.setEmailVerified(true);
        user.setEnabled(true);
        final User createdUser = User.builder()
                .name(input.getName())
                .surname(input.getSurname())
                .email(input.getEmail())
                .address(input.getAddress())
                .alerting(input.isAlerting())
                .username(input.getUsername())
                .energyAlertingThreshold(input.getEnergyAlertingThreshold())
                .build();
        CredentialRepresentation passwordCredential = new CredentialRepresentation();
        passwordCredential.setType("password");
        passwordCredential.setTemporary(false);
        passwordCredential.setValue(input.getPassword());

        List<CredentialRepresentation> credentials = new ArrayList<>();
        credentials.add(passwordCredential);
        user.setCredentials(credentials);
        try(var createUserResponse = realmResource.users().create(user)){
            System.out.println("==================keycloak response: " + createUserResponse.getStatus());
            if(createUserResponse.getStatus() == HttpStatus.SC_CREATED){
                log.info("Creating user in the keycloak");
                String path = createUserResponse.getLocation().getPath();
                String keycloakId = path.substring(path.lastIndexOf('/')+1);
                createdUser.setKeycloakId(keycloakId);
                try {
                    System.out.println("=====ABout to save in db======");
                    log.info("Creating user in the db");
                    final User saved = userRepository.save(createdUser);
                    System.out.println("======== saved in db =======");
                    return toDto(saved);
                }catch (Exception exe){
                    System.out.println("====failed to save in db=====");
                    realmResource.users().delete(keycloakId);
                    throw new RuntimeException("Failed to save user, rolled back keycloak user");

                }
            }
            System.out.println("========keycloak creation failed, status: " + createUserResponse.getStatus());
            throw new RuntimeException("Failed to create user");
        }
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
                .username(user.getUsername())
                .energyAlertingThreshold(user.getEnergyAlertingThreshold())
                .build();

    }
}
