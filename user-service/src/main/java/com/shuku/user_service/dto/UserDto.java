package com.shuku.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // for getters and setters
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String name;
    private String surname;
    private String email;
    private String address;
    private String username;
    private String password;
    private boolean alerting; // user can choose to have alerting or not
    private double energyAlertingThreshold; // if user chooses to have alerting then we need a threshold for it

}
