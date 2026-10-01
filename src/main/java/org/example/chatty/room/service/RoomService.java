package org.example.chatty.room.service;

import org.example.chatty.room.dto.AddRoomMembersRequest;
import org.example.chatty.room.dto.CreateRoomRequest;
import org.example.chatty.room.dto.RoomResponse;
import org.example.chatty.room.entity.Room;
import org.example.chatty.room.entity.RoomMember;
import org.example.chatty.room.entity.RoomRole;
import org.example.chatty.room.entity.RoomType;
import org.example.chatty.room.mapper.RoomMapper;
import org.example.chatty.room.repository.RoomMemberRepository;
import org.example.chatty.room.repository.RoomRepository;
import org.example.chatty.user.entity.User;
import org.example.chatty.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final UserRepository userRepository;
    private final RoomMapper roomMapper;


    public RoomService(RoomRepository roomRepository, RoomMemberRepository roomMemberRepository, UserRepository userRepository, RoomMapper roomMapper) {
        this.roomRepository = roomRepository;
        this.roomMemberRepository = roomMemberRepository;
        this.userRepository = userRepository;
        this.roomMapper = roomMapper;
    }

    @Transactional()
    public RoomResponse createRoom(CreateRoomRequest request) {
        User currentUser = getCurrentUser();
        validateCreateRoomRequest(request, currentUser);
        Room room = new Room();
        room.setType(request.type());
        room.setName(request.name());
        room.setCreatedBy(currentUser);
        room.setCreatedAt(LocalDateTime.now());
        Room savedRoom = roomRepository.save(room);
        RoomMember owner = new RoomMember();
        owner.setRoom(savedRoom);
        owner.setUser(currentUser);
        owner.setRole(RoomRole.OWNER);
        owner.setJoinedAt(LocalDateTime.now());
        List<RoomMember> members = request.memberIds().stream().map(memberId -> createMember(memberId, savedRoom)).toList();
        List<RoomMember> allMembers = new java.util.ArrayList<>();
        allMembers.add(owner);
        allMembers.addAll(members);
        roomMemberRepository.saveAll(allMembers);
        savedRoom.setMembers(allMembers);
        return roomMapper.toRoomResponse(savedRoom);
    }

    public List<RoomResponse> getUserRooms() {
        User currentUser = getCurrentUser();
        return roomMemberRepository.findByUserId(currentUser.getId()).stream().map(RoomMember::getRoom).map(roomMapper::toRoomResponse).toList();
    }

    public RoomResponse getRoom(UUID roomId) {
        User currentUser = getCurrentUser();
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));

        boolean isMember = roomMemberRepository.existsByRoomAndUser(room, currentUser);
        if (!isMember) throw new RuntimeException("You are not a member of this room");
        return roomMapper.toRoomResponse(room);
    }

    @Transactional
    public RoomResponse addRoomMembers(UUID roomId, AddRoomMembersRequest request) {
        User currentUser = getCurrentUser();

        if (request == null || request.userIds() == null || request.userIds().isEmpty()) {
            throw new IllegalArgumentException("At least one user is required");
        }

        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));

        if (room.getType() == RoomType.PRIVATE) {
            throw new RuntimeException("Members cannot be added to a private room");
        }

        RoomMember currentUserMember = roomMemberRepository.findByRoomAndUser(room, currentUser);

        if (currentUserMember == null) {
            throw new RuntimeException("You are not a member of this room");
        }

        if (currentUserMember.getRole() != RoomRole.OWNER) {
            throw new RuntimeException("Only the owner can add members");
        }

        List<RoomMember> currentMembers = roomMemberRepository.findByRoom(room);

        Set<UUID> currentMemberIds = currentMembers.stream().map(member ->
                member.getUser().getId()).collect(Collectors.toSet());

        List<RoomMember> newMembers = request.userIds()
                .stream()
                .distinct()
                .filter(userId -> !currentMemberIds.contains(userId))
                .map(userId -> createMember(userId, room))
                .toList();

        roomMemberRepository.saveAll(newMembers);

        currentMembers.addAll(newMembers);
        room.setMembers(currentMembers);

        return roomMapper.toRoomResponse(room);
    }

    public void removeRoomMember(UUID roomId, UUID userId) {
        User currentUser = getCurrentUser();

        if (currentUser.getId().equals(userId)) throw new RuntimeException("You cannot remove yourself from the room");


        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room Not Found"));

        RoomMember member = roomMemberRepository.findByRoomAndUser(room, currentUser);

        if (member == null) throw new RuntimeException("You are not a member of this room");

        if (member.getRole() != RoomRole.OWNER) throw new RuntimeException("Only the room owner can remove members");

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("the user you want to delete not found"));

        if (!roomMemberRepository.existsByRoomAndUser(room, user))
            throw new RuntimeException("The user is not a member of this room");

        RoomMember targetMember = roomMemberRepository.findByRoomAndUser(room, user);
        roomMemberRepository.delete(targetMember);
    }

    @Transactional
    public void leaveRoom(UUID roomId) {
        User currentUser = getCurrentUser();

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new RuntimeException("Room not found"));

        RoomMember member =
                roomMemberRepository.findByRoomAndUser(
                        room,
                        currentUser
                );

        if (member == null) {
            throw new RuntimeException(
                    "You are not a member of this room"
            );
        }

        if (member.getRole() == RoomRole.OWNER) {
            throw new RuntimeException(
                    "The room owner cannot leave the room"
            );
        }

        roomMemberRepository.delete(member);
    }

    public void deleteRoom(UUID roomId) {
        User currentUser = getCurrentUser();
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));

        RoomMember ownerMember = roomMemberRepository.findByRoomAndUser(room, currentUser);
        if (ownerMember == null || ownerMember.getRole() != RoomRole.OWNER) {
            throw new RuntimeException("Only the room owner can delete the room");
        }
        roomRepository.delete(room);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUserName(authentication.getName()).orElseThrow(() -> new RuntimeException("User not found"));
    }

    private void validateCreateRoomRequest(CreateRoomRequest request, User currentUser) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (request.type() == null) {
            throw new IllegalArgumentException("Room type is required");
        }
        if (request.memberIds() == null || request.memberIds().isEmpty()) {
            throw new IllegalArgumentException("At least one member is required");
        }
        if (request.type() == RoomType.PRIVATE) {
            validatePrivateRoom(request, currentUser);
        }
        if (request.type() == RoomType.GROUP) {
            validateGroupRoom(request, currentUser);
        }
    }

    private void validatePrivateRoom(CreateRoomRequest request, User currentUser) {
        if (request.memberIds().size() != 1) {
            throw new IllegalArgumentException("Private room must have exactly one other member");
        }
        if (request.memberIds().contains(currentUser.getId())) {
            throw new IllegalArgumentException("You cannot create a private room with yourself");
        }
    }

    private void validateGroupRoom(CreateRoomRequest request, User currentUser) {
        if (request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("Group room name is required");
        }
        if (request.memberIds().contains(currentUser.getId())) {
            throw new IllegalArgumentException("Creator should not be included in memberIds");
        }
    }

    private RoomMember createMember(UUID userId, Room room) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        RoomMember member = new RoomMember();
        member.setRoom(room);
        member.setUser(user);
        member.setRole(RoomRole.MEMBER);
        member.setJoinedAt(LocalDateTime.now());
        return member;
    }

}
