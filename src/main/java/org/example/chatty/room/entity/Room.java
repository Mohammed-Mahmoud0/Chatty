package org.example.chatty.room.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.chatty.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
public class Room {
    @Id
    @GeneratedValue
    private UUID id;
    @Enumerated(EnumType.STRING)
    private RoomType type;
    private String name;
    private LocalDateTime createdAt;
    @ManyToOne
    private User createdBy;

    @OneToMany(mappedBy = "room")
    private List<RoomMember> members;
}
