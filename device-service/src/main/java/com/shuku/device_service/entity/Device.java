package com.shuku.device_service.entity;


import com.shuku.device_service.model.DeviceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "device")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    public DeviceType type;

    public String location;

    private Long userId;


}
