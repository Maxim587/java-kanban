package httpserver;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.educationmm.taskmanager.main.httpserver.HttpTaskServer;
import ru.educationmm.taskmanager.main.model.Task;
import ru.educationmm.taskmanager.main.model.TaskStatus;
import ru.educationmm.taskmanager.main.service.InMemoryTaskManager;
import ru.educationmm.taskmanager.main.service.TaskManager;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpServerCommonTests {

    protected TaskManager taskManager = HttpTaskServer.getTaskManager();
    protected HttpClient client = HttpClient.newHttpClient();

    @BeforeEach
    public void prepare() throws NoSuchFieldException, IllegalAccessException {
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubtasks();
        taskManager.deleteAllEpics();
        Field id = InMemoryTaskManager.class.getDeclaredField("id");
        id.setAccessible(true);
        id.setInt(taskManager, 0);
        HttpTaskServer.start();
    }

    @AfterEach
    public void shutDown() {
        HttpTaskServer.stop();
    }

    @Test
    public void getHistoryTest() throws IOException, InterruptedException {
        Task task = new Task("Test get list", "Test task", TaskStatus.NEW, 5, LocalDateTime.now());
        Task task2 = new Task("Test get list 2", "Test task2", TaskStatus.NEW, 5, LocalDateTime.now().plusDays(1));
        taskManager.addTask(task);
        taskManager.addTask(task2);
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task2.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");

        //количество задач в менеджере равно количеству в ответе
        String responseBody = new String(response.body().getBytes(StandardCharsets.UTF_8));
        JsonElement jsonElement = JsonParser.parseString(responseBody);
        Assertions.assertTrue(jsonElement.isJsonArray(), "Некорректное количество задач");
        assertEquals(taskManager.getHistory().size(), jsonElement.getAsJsonArray().size(), "Некорректное количество задач");
    }

    @Test
    public void getPrioritizedTest() throws IOException, InterruptedException {
        Task task = new Task("Test get list", "Test task", TaskStatus.NEW, 5, LocalDateTime.now());
        Task task2 = new Task("Test get list 2", "Test task2", TaskStatus.NEW, 5, LocalDateTime.now().plusDays(1));
        taskManager.addTask(task);
        taskManager.addTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");

        //количество задач в менеджере равно количеству в ответе
        String responseBody = new String(response.body().getBytes(StandardCharsets.UTF_8));
        JsonElement jsonElement = JsonParser.parseString(responseBody);
        Assertions.assertTrue(jsonElement.isJsonArray(), "Некорректное количество задач");
        assertEquals(taskManager.getPrioritizedTasks().size(), jsonElement.getAsJsonArray().size(), "Некорректное количество задач");
    }
}