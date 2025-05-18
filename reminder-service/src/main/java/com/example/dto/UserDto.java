package com.example.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for User-service responses.
 * Add fields here to match exactly what the User-service returns.
 */
@Getter
@Setter
@Data
public class UserDto {
    private String id;
    private String email;
    private String name;
}
