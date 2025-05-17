package com.example.command;

import org.springframework.stereotype.Component;

import java.util.Stack;
@Component
public class CommandManager {
    private final Stack<Command> history = new Stack<>();
    private final Stack<Command> redoStack = new Stack<>();

    public void executeCommand(Command command) {
        command.execute();
        history.push(command);
        redoStack.clear();
    }

    public boolean canUndo() {
        return !history.isEmpty();
    }

    public void undoLast() {
        if (canUndo()) {
            Command command = history.pop();
            command.undo();
            redoStack.push(command);
        }
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public void redoLast() {
        if (canRedo()) {
            Command command = redoStack.pop();
            command.execute();
            history.push(command);
        }
    }
}