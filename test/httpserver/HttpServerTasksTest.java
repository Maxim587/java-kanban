package httpserver;

import com.google.gson.JsonElement;
import ru.educationmm.taskmanager.main.model.Task;
import ru.educationmm.taskmanager.main.model.TaskStatus;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

class HttpServerTasksTest extends AbstractHttpTaskManagerTasksTest<Task> {

    public HttpServerTasksTest() {
        this.url = URI.create("http://localhost:8080/tasks");
    }

    @Override
    public Task createTask(String name, String description, LocalDateTime startTime) {
        return new Task(name, description, TaskStatus.NEW, 5, startTime);
    }

    @Override
    public Task getTaskFromJson(JsonElement jsonElement) {
        return gson.fromJson(jsonElement, Task.class);
    }

    @Override
    public List<Task> getTasksFromManager() {
        return taskManager.getTasks();
    }

    @Override
    public void addToManager(Task task) {
        taskManager.addTask(task);
    }
}