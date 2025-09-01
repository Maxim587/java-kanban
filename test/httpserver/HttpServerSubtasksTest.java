package httpserver;

import com.google.gson.JsonElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import ru.educationmm.taskmanager.main.model.Epic;
import ru.educationmm.taskmanager.main.model.Subtask;
import ru.educationmm.taskmanager.main.model.TaskStatus;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

class HttpServerSubtasksTest extends AbstractHttpTaskManagerTasksTest<Subtask> {

    public HttpServerSubtasksTest() {
        this.url = URI.create("http://localhost:8080/subtasks");
    }

    @Override
    @BeforeEach
    public void prepare() throws NoSuchFieldException, IllegalAccessException {
        super.prepare();
        Epic epic = new Epic("Test subtasks", "Test subtasks");
        epic.setId(10);
        taskManager.addTask(epic);
    }

    @Override
    public Subtask createTask(String name, String description, LocalDateTime startTime) {
        return new Subtask(name, description, 10, TaskStatus.NEW, 5, startTime);
    }

    @Override
    public Subtask getTaskFromJson(JsonElement jsonElement) {
        return gson.fromJson(jsonElement, Subtask.class);
    }

    @Override
    public List<Subtask> getTasksFromManager() {
        return taskManager.getSubtasks();
    }

    @Override
    public void addToManager(Subtask task) {
        taskManager.addTask(task);
    }

    @Override
    public void checkTasksEquals(Subtask task1, Subtask task2) {
        super.checkTasksEquals(task1, task2);
        Assertions.assertEquals(task1.getEpicId(), task2.getEpicId(), "EpicId подзадач не совпадают");
    }
}