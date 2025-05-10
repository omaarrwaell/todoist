package  com.example.composite;
import lombok.Data;
import java.util.Collections;
import java.util.List;


@Data
public class TaskLeaf implements TaskComponent {
    private String id;
    private String title;
    private String description;
    private boolean completed;

    public TaskLeaf(String id, String title) {
        this.id = id;
        this.title = title;
        this.completed = false;
    }

    @Override
    public void markComplete() {
        this.completed = true;
        System.out.println("Task '" + title + "' marked as complete");
    }

    @Override
    public boolean isComplete() {
        return completed;
    }

    @Override
    public void addSubTask(TaskComponent task) {
        throw new UnsupportedOperationException("Cannot add subtasks to a leaf task");
    }

    @Override
    public void removeSubTask(TaskComponent task) {
        throw new UnsupportedOperationException("Cannot remove subtasks from a leaf task");
    }

    @Override
    public TaskComponent getSubTask(int index) {
        throw new UnsupportedOperationException("Leaf tasks don't have subtasks");
    }

    @Override
    public List<TaskComponent> getSubTasks() {
        return Collections.emptyList();  // Return empty list instead of null
    }

    @Override
    public void displayDetails() {
        System.out.println("Task: " + title + " [" + (completed ? "Completed" : "Pending") + "]");
    }

    @Override
    public int getTaskCount() {
        return 1;  // A leaf is just one task
    }
}