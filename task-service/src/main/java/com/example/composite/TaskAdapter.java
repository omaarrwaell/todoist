package com.example.composite;

import com.example.composite.TaskComponent;
import com.example.composite.TaskComposite;
import com.example.composite.TaskLeaf;
import com.example.models.Task;
import com.example.repositories.TaskRepository;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TaskAdapter {

    private final TaskRepository taskRepository;

    public TaskAdapter(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Convert a database Task to a TaskComponent (either Leaf or Composite)
     */
    public TaskComponent toTaskComponent(Task task) {
        if (task == null) {
            return null;
        }

        TaskComponent component;

        // Determine if this should be a leaf or composite
        if (task.getChildrenIds() == null || task.getChildrenIds().isEmpty()) {
            // Create a leaf task
            component = new TaskLeaf(task.getId(), task.getTitle());
            ((TaskLeaf)component).setDescription(task.getDescription());
            if (task.isCompleted()) {
                component.markComplete();
            } else {
                ((TaskLeaf)component).setCompleted(false);
            }
            ((TaskLeaf)component).setTags(task.getTags() != null ? task.getTags() : new ArrayList<>());
            ((TaskLeaf)component).setPriority(task.getPriority());
            ((TaskLeaf)component).setFlag(task.getFlag());
        } else {
            // Create a composite task
            component = new TaskComposite(task.getId(), task.getTitle());
            ((TaskComposite)component).setDescription(task.getDescription());
            if (task.isCompleted()) {
                // We don't use markComplete() here to avoid cascade to children, which we'll load separately
                ((TaskComposite)component).setCompleted(true);
            } else {
                ((TaskComposite)component).setCompleted(false);
            }
            ((TaskComposite)component).setTags(task.getTags() != null ? task.getTags() : new ArrayList<>());
            ((TaskComposite)component).setPriority(task.getPriority());
            ((TaskComposite)component).setFlag(task.getFlag());

            // Recursively add children
            for (String childId : task.getChildrenIds()) {
                Task childTask = taskRepository.findById(childId).orElse(null);
                if (childTask != null) {
                    ((TaskComposite)component).addSubTask(toTaskComponent(childTask));
                }
            }
        }

        return component;
    }
}