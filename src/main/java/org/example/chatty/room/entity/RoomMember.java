package org.example.chatty.room.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.chatty.user.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"room_id", "user_id"})
        }
)
@Setter
@Getter
public class RoomMember {
    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne
    private Room room;
    @ManyToOne
    private User user;
    @Enumerated(EnumType.STRING)
    private RoomRole role;
    private LocalDateTime joinedAt;
}
