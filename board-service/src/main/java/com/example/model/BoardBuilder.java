package com.example.model;

import java.util.ArrayList;
import java.util.HashMap;

public class BoardBuilder {
    private final Board board;

    public BoardBuilder() {
        board = new Board();
        board.setMemberUserIds(new ArrayList<>());
        board.setUserRoles(new HashMap<>());
        board.setTaskIds(new ArrayList<>());
    }

    public BoardBuilder setName(String name) {
        board.setName(name);
        return this;
    }

    public BoardBuilder setCategory(String category) {
        board.setCategory(category);
        return this;
    }

    public BoardBuilder setAdmin(String adminUserId) {
        board.setAdminUserId(adminUserId);
        board.getMemberUserIds().add(adminUserId);
        board.getUserRoles().put(adminUserId, Role.ADMIN);
        return this;
    }

    public BoardBuilder addUser(String userId, Role role) {
        board.getMemberUserIds().add(userId);
        board.getUserRoles().put(userId, role);
        return this;
    }

    public BoardBuilder addTask(String taskId) {
        board.getTaskIds().add(taskId);
        return this;
    }

    public Board build() {
        return board;
    }
}
