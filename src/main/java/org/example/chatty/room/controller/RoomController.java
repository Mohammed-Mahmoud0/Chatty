package org.example.chatty.room.controller;

import org.example.chatty.room.dto.AddRoomMembersRequest;
import org.example.chatty.room.dto.CreateRoomRequest;
import org.example.chatty.room.dto.RoomResponse;
import org.example.chatty.room.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/rooms")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomResponse createRoom(@RequestBody CreateRoomRequest request) {
        return roomService.createRoom(request);
    }

    @GetMapping
    public List<RoomResponse> getRooms() {
        return roomService.getUserRooms();
    }

    @GetMapping("/{roomId}")
    public RoomResponse getRoom(@PathVariable UUID roomId) {
        return roomService.getRoom(roomId);
    }

    @PostMapping("/{roomId}/members")
    public RoomResponse addRoomMembers(@PathVariable UUID roomId, @RequestBody AddRoomMembersRequest request) {
        return roomService.addRoomMembers(roomId, request);
    }

    @DeleteMapping("/{roomId}/members/{userId}")
    public void removeRoomMember(@PathVariable UUID roomId, @PathVariable UUID userId) {
        roomService.removeRoomMember(roomId, userId);
    }

    @DeleteMapping("/{roomId}/leave")
    public void leaveRoom(@PathVariable UUID roomId) {
        roomService.leaveRoom(roomId);
    }

    @DeleteMapping("/{roomId}")
    public void deleteRoom(@PathVariable UUID roomId) {
        roomService.deleteRoom(roomId);
    }
}
