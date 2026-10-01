package org.example.chatty.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.chatty.room.entity.Room;
import org.example.chatty.room.entity.RoomMember;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
@Table(name = "users")  // Tell Hibernate to use the "users" table
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(unique = true)
    private String userName;
    @Column(unique = true)
    private String email;
    private String password;
    private boolean status;
    private LocalDateTime lastSeen;
    private LocalDateTime createdAt;
    private String imageName;
    private String imageType;
    @Lob
    private byte[] imageData;

    @OneToMany(mappedBy = "createdBy")
    private List<Room> createdRooms;

    @OneToMany(mappedBy = "user")
    private List<RoomMember> roomMemberships;
}
