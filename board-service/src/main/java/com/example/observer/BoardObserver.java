package com.example.observer;

public interface BoardObserver {
    void update(String boardId, String action, String payload);
}
