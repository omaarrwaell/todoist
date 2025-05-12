package com.example.observer;

public interface BoardSubject {
    void attach(BoardObserver observer);
    void detach(BoardObserver observer);
    void notifyObservers(String boardId, String action, String message);
}