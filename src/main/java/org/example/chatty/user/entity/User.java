package org.example.chatty.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
