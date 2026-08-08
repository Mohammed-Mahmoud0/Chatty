package org.example.chatty.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Setter
@Getter
@Table(name = "users")  // Tell Hibernate to use the "users" table
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String userName;
    private String email;
    private String password;
    private boolean status;
    private LocalDateTime lastSeen;
    private LocalDateTime createdAt;
    private String imageName;
    private String imageType;
    @Lob
    private byte[] imageData;
}
