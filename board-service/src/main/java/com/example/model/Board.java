package com.example.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Getter
@Setter
@Document(collection = "boards")
public class Board {
    @Id
    private String id;

    private String name;
    private String category;
    private String adminUserId;

    private List<String> memberUserIds;
    private Map<String, Role> userRoles;// userId -> Role
    private List<String> taskIds;

    public void setUserRoles(String userId, Role role) {
        if(userRoles == null) {
            userRoles = new HashMap<>();
        }
        userRoles.put(userId,role);
    }
}
