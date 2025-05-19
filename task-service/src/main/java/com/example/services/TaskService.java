package com.example.services;

import com.example.composite.TaskAdapter;
import com.example.composite.TaskComponent;
import com.example.composite.TaskComposite;
import com.example.composite.TaskLeaf;
import com.example.models.Task;
import com.example.models.TaskFlag;
import com.example.repositories.TaskRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskAdapter taskAdapter;

    @Autowired
    public TaskService(TaskRepository taskRepository, TaskAdapter taskAdapter) {
        this.taskRepository = taskRepository;
        this.taskAdapter = taskAdapter;
    }

    //----------------------
    // TASK MANAGEMENT WITH COMPOSITE PATTERN
    //----------------------

    /**
     * Create a new task from a TaskLeaf component
     */


    public TaskLeaf createTask(TaskLeaf taskLeaf) {
        // Convert the TaskLeaf to a database Task
        Task task = new Task();
        task.setTitle(taskLeaf.getTitle());
        task.setDescription(taskLeaf.getDescription());
        task.setCompleted(taskLeaf.isComplete());
        task.setTags(taskLeaf.getTags());
        task.setPriority(taskLeaf.getPriority());
        task.setFlag(taskLeaf.getFlag());
        task.setCreatedAt(new Date());
        task.setUpdatedAt(new Date());
        task.setSubtask(false);
        task.setChildrenIds(new ArrayList<>());

        // Save the task to the database
        Task savedTask = taskRepository.save(task);

        // Update the TaskLeaf with the generated ID
        taskLeaf.setId(savedTask.getId());

        return taskLeaf;
    }

    @Transactional
    public TaskComponent assignDueDate(String taskId, Date dueDate, boolean propagate) {
        TaskComponent component = getTaskAsComponent(taskId);

        // Set due date on the component using reflection (since it's not in the interface)
        if (component instanceof TaskLeaf) {
            TaskLeaf leaf = (TaskLeaf) component;
            leaf.setDueDate(dueDate);
        } else if (component instanceof TaskComposite) {
            TaskComposite composite = (TaskComposite) component;
            composite.setDueDate(dueDate);

            if (propagate) {
                propagateDueDate(composite, dueDate);
            }
        }

        // Update the component in the database
        updateTaskComponent(component);

        // Return the updated component
        return getTaskAsComponent(taskId);
    }

    /**
     * Helper method to propagate due date to all subtasks recursively
     */
    private void propagateDueDate(TaskComposite composite, Date dueDate) {
        for (TaskComponent child : composite.getSubTasks()) {
            // Set due date on the child
            if (child instanceof TaskLeaf) {
                ((TaskLeaf) child).setDueDate(dueDate);
            } else if (child instanceof TaskComposite) {
                TaskComposite childComposite = (TaskComposite) child;
                childComposite.setDueDate(dueDate);
                // Recursively propagate to this child's subtasks
                propagateDueDate(childComposite, dueDate);
            }
        }
    }

    @Transactional
    public TaskLeaf createSubtask(String parentId, TaskLeaf subtaskLeaf) {
        Task parentTask = getTaskById(parentId);
        TaskComponent parentComponent = getTaskAsComponent(parentId);


        if (subtaskLeaf.getTags() == null || subtaskLeaf.getTags().isEmpty()) {
            subtaskLeaf.setTags(new ArrayList<>(parentComponent.getTags()));
        }

        if (subtaskLeaf.getPriority() == null) {
            subtaskLeaf.setPriority(parentComponent.getPriority());
        }

        if (subtaskLeaf.getFlag() == null) {
            subtaskLeaf.setFlag(parentComponent.getFlag());
        }

        // Inherit assigned user - ensure this gets transferred
        if (parentComponent.getAssignedUserId() != null) {
            subtaskLeaf.assignToUser(parentComponent.getAssignedUserId());
        }

        // Convert subtask to database entity
        Task subtask = new Task();
        subtask.setTitle(subtaskLeaf.getTitle());
        subtask.setDescription(subtaskLeaf.getDescription());
        subtask.setCompleted(subtaskLeaf.isComplete());
        subtask.setTags(subtaskLeaf.getTags());
        subtask.setPriority(subtaskLeaf.getPriority());
        subtask.setFlag(subtaskLeaf.getFlag());

        // Set the assignedUserId from the leaf
        subtask.setAssignedUserId(subtaskLeaf.getAssignedUserId());

        subtask.setCreatedAt(new Date());
        subtask.setUpdatedAt(new Date());
        subtask.setParentId(parentId);

        // Ensure subtask is marked as a subtask
        subtask.setSubtask(true);

        subtask.setChildrenIds(new ArrayList<>());

        // Save subtask
        Task savedSubtask = taskRepository.save(subtask);

        // Update parent's children list
        if (parentTask.getChildrenIds() == null) {
            parentTask.setChildrenIds(new ArrayList<>());
        }
        parentTask.getChildrenIds().add(savedSubtask.getId());
        taskRepository.save(parentTask);

        // Update the TaskLeaf with the generated ID
        subtaskLeaf.setId(savedSubtask.getId());

        return subtaskLeaf;
    }
    /**
     * Get a task as a TaskComponent (either TaskLeaf or TaskComposite)
     */
    public TaskComponent getTaskAsComponent(String id) {
        Task task = getTaskById(id);
        return taskAdapter.toTaskComponent(task);
    }

    /**
     * Update a task from a TaskComponent (either TaskLeaf or TaskComposite)
     */
    @Transactional
    public TaskComponent updateTaskComponent(TaskComponent component) {
        if (component == null || component.getId() == null) {
            throw new IllegalArgumentException("Cannot update a null task or a task without ID");
        }

        Task task = getTaskById(component.getId());

        // Update basic properties
        task.setTitle(component.getTitle());
        task.setCompleted(component.isComplete());

        if (component instanceof TaskLeaf) {
            TaskLeaf leaf = (TaskLeaf) component;
            task.setDescription(leaf.getDescription());
            task.setTags(leaf.getTags());
            task.setPriority(leaf.getPriority());
            task.setFlag(leaf.getFlag());
            task.setAssignedUserId(leaf.getAssignedUserId());
            task.setDueDate(leaf.getDueDate());
        } else if (component instanceof TaskComposite) {
            TaskComposite composite = (TaskComposite) component;
            task.setDescription(composite.getDescription());
            task.setTags(composite.getTags());
            task.setPriority(composite.getPriority());
            task.setFlag(composite.getFlag());
            task.setAssignedUserId(composite.getAssignedUserId());
            task.setDueDate(composite.getDueDate());

            // Update children list
            List<String> childrenIds = composite.getSubTasks().stream()
                    .map(TaskComponent::getId)
                    .collect(Collectors.toList());
            task.setChildrenIds(childrenIds);

            // Recursively update children
            for (TaskComponent child : composite.getSubTasks()) {
                updateTaskComponent(child);
            }
        }

        task.setUpdatedAt(new Date());
        taskRepository.save(task);

        return component;
    }

    /**
     * Mark a task complete using the composite pattern (will cascade to subtasks)
     */
    @Transactional
    public void markTaskComplete(String id) {
        TaskComponent component = getTaskAsComponent(id);
        component.markComplete(); // This will cascade to children automatically
        updateTaskComponent(component);
    }

    //----------------------
    // BASIC DATABASE OPERATIONS
    //----------------------

    /**
     * Get a task by ID directly from the database
     */
    public Task getTaskById(String id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }

    /**
     * Get all top-level tasks
     */
    public List<Task> getAllTasks() {
        return taskRepository.findByParentIdIsNull();
    }

    /**
     * Delete a task and all its subtasks
     */
    @Transactional
    public void deleteTask(String id) {
        Task task = getTaskById(id);

        // First, remove this task from its parent
        if (task.getParentId() != null) {
            Task parent = getTaskById(task.getParentId());
            if (parent.getChildrenIds() != null) {
                parent.getChildrenIds().remove(id);
                taskRepository.save(parent);
            }
        }

        // Then, recursively delete all children
        if (task.getChildrenIds() != null) {
            List<String> childrenToDelete = new ArrayList<>(task.getChildrenIds());
            for (String childId : childrenToDelete) {
                deleteTask(childId);
            }
        }

        // Finally, delete this task
        taskRepository.deleteById(id);
    }

    //----------------------
    // FEATURE-SPECIFIC OPERATIONS
    //----------------------

    /**
     * Feature 1: Filter tasks by tag
     */
    public List<Task> filterTasksByTag(String tag) {
        return taskRepository.findByTagsContaining(tag);
    }

    /**
     * Feature 2: Update a task's priority
     */
    @Transactional
    public void updateTaskPriority(String id, String priority, boolean propagate) {
        if (propagate) {
            // Use the composite pattern for propagation
            TaskComponent component = getTaskAsComponent(id);
            if (component instanceof TaskComposite) {
                TaskComposite composite = (TaskComposite) component;
                // Set priority for the parent and all children
                composite.setPriority(priority);
                for (TaskComponent child : composite.getSubTasks()) {
                    child.setPriority(priority);
                }
                updateTaskComponent(composite);
            } else {
                // It's a leaf, just update it
                component.setPriority(priority);
                updateTaskComponent(component);
            }
        } else {
            // Just update this task without propagation
            Task task = getTaskById(id);
            task.setPriority(priority);
            task.setUpdatedAt(new Date());
            taskRepository.save(task);
        }
    }

    /**
     * Feature 3: Assign a task to a flag
     */
    /**
     * Feature 3: Assign a task to a flag
     */
    @Transactional
    public void assignTaskToFlag(String id, TaskFlag flag, boolean propagate) {
        if (propagate) {
            // Use the composite pattern for propagation
            TaskComponent component = getTaskAsComponent(id);
            if (component instanceof TaskComposite) {
                TaskComposite composite = (TaskComposite) component;
                // Set flag for parent and all children
                composite.propagateFlag(flag);
                updateTaskComponent(composite);
            } else {
                // It's a leaf, just update it
                component.setFlag(flag);
                updateTaskComponent(component);
            }
        } else {
            // Just update this task without propagation
            Task task = getTaskById(id);
            task.setFlag(flag);
            task.setUpdatedAt(new Date());
            taskRepository.save(task);
        }
    }
    /**
     * Add a tag to a task and all its subtasks using the Composite pattern
     */
    @Transactional
    public void addTagToTaskAndChildren(String taskId, String tag) {
        // Get the task as a composite component
        TaskComponent component = getTaskAsComponent(taskId);

        // Use the composite pattern to add the tag to this component and all children
        addTagToComponentHierarchy(component, tag);

        // Save the changes back to the database
        updateTaskComponent(component);
    }


    private void addTagToComponentHierarchy(TaskComponent component, String tag) {
        // Add the tag to this component
        component.addTag(tag);

        // If this is a composite, add the tag to all children
        if (component instanceof TaskComposite) {
            for (TaskComponent child : component.getSubTasks()) {
                addTagToComponentHierarchy(child, tag);
            }
        }
    }
    /**
     * Remove a tag from a task and all its subtasks using the Composite pattern
     */
    @Transactional
    public void removeTagFromTaskAndChildren(String taskId, String tag) {
        TaskComponent component = getTaskAsComponent(taskId);

        removeTagFromComponentHierarchy(component, tag);

        updateTaskComponent(component);
    }


    private void removeTagFromComponentHierarchy(TaskComponent component, String tag) {
        // Remove the tag from this component
        component.removeTag(tag);

        // If this is a composite, remove the tag from all children
        if (component instanceof TaskComposite) {
            for (TaskComponent child : component.getSubTasks()) {
                removeTagFromComponentHierarchy(child, tag);
            }
        }
    }
    /**
     * Feature 4: Sort tasks by date
     */
    public List<Task> getTasksSortedByDueDate(boolean ascending) {
        if (ascending) {
            return taskRepository.findAllByOrderByDueDateAsc();
        } else {
            return taskRepository.findAllByOrderByDueDateDesc();
        }
    }

    /**
     * Get the total count of a task and all its subtasks
     */
    public int getTaskCount(String id) {
        TaskComponent component = getTaskAsComponent(id);
        return component.getTaskCount();
    }

}