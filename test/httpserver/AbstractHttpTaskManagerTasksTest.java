package httpserver;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.educationmm.taskmanager.main.httpserver.HttpTaskServer;
import ru.educationmm.taskmanager.main.httpserver.handler.BaseHttpHandler;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class AbstractHttpTaskManagerTasksTest<T extends Task> {

    protected TaskManager taskManager = HttpTaskServer.getTaskManager();
    protected Gson gson = new BaseHttpHandler(taskManager).getGson();
    protected HttpClient client = HttpClient.newHttpClient();
    protected URI url;

    abstract public T createTask(String name, String description, LocalDateTime startTime);

    abstract public T getTaskFromJson(JsonElement jsonElement);

    abstract public List<T> getTasksFromManager();

    abstract public void addToManager(T task);

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
    public void testGetTasks() throws IOException, InterruptedException {
        T task = createTask("Test get list", "Test task", LocalDateTime.now());
        T task2 = createTask("Test get list 2", "Test task2", LocalDateTime.now().plusDays(1));
        task.setId(1);
        task2.setId(2);
        addToManager(task);
        addToManager(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");

        //количество задач в менеджере равно количеству в ответе
        String responseBody = new String(response.body().getBytes(StandardCharsets.UTF_8));
        JsonElement jsonElement = JsonParser.parseString(responseBody);
        Assertions.assertTrue(jsonElement.isJsonArray(), "Некорректное количество задач");
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertEquals(2, jsonArray.size(), "Некорректное количество задач");

        //все поля задачи из менеджера совпадают с полями полученной задачи
        T taskFromResponse = getTaskFromJson(jsonArray.get(0));
        checkTasksEquals(task, taskFromResponse);
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        T task = createTask("Test get by id", "Test task", LocalDateTime.now());
        T task2 = createTask("Test get by id 2", "Test task2", LocalDateTime.now().plusDays(1));
        addToManager(task);
        addToManager(task2);

        String requestedTaskId = "2";
        String urlWithParams = url.toString() + "/" + requestedTaskId;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlWithParams))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");

        String responseBody = new String(response.body().getBytes(StandardCharsets.UTF_8));
        JsonElement jsonElement = JsonParser.parseString(responseBody);

        //получена только 1 задача
        Assertions.assertTrue(jsonElement.isJsonObject(), "Некорректное количество задач");

        //все поля задачи из менеджера совпадают с полями полученной задачи
        T taskFromResponse = getTaskFromJson(jsonElement);
        checkTasksEquals(task2, taskFromResponse);

        //запрос на получение несуществующей задачи
        String nonexistentTaskId = "100";
        urlWithParams = url.toString() + "/" + nonexistentTaskId;
        request = HttpRequest.newBuilder()
                .uri(URI.create(urlWithParams))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Код ответа должен быть 404");
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        T task = createTask("Test add task", "Test task", LocalDateTime.now());
        String taskJson = gson.toJson(task);
        task.setId(1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Код ответа должен быть 201");

        //должна быть создана 1 задача в менеджере
        List<T> tasksFromManager = getTasksFromManager();
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");

        //поля переданной задачи совпадают с полями из задачи из менеджера
        T taskFromManager = tasksFromManager.getFirst();
        checkTasksEquals(task, taskFromManager);
    }

    @Test
    public void testTaskIntersection() throws IOException, InterruptedException {
        T task = createTask("Test add task", "Test task", LocalDateTime.now());
        String taskJson = gson.toJson(task);
        addToManager(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        //добавление пересекающейся по времени задачи (повторное добавление той же задачи)
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Код ответа должен быть 406");

    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        T task = createTask("Test update", "Test task", LocalDateTime.now());
        task.setId(1);
        addToManager(task);

        T updatedTask = createTask(task.getName(), task.getDescription(), task.getStartTime());
        updatedTask.setId(1);
        updatedTask.setName("updated name");
        updatedTask.setStatus(TaskStatus.IN_PROGRESS);

        String taskJson = gson.toJson(updatedTask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");

        //поля name и status должны обновиться, остальные - нет
        T taskFromManager = getTasksFromManager().getFirst();
        checkTasksEquals(updatedTask, taskFromManager);

        //обновление несуществующей задачи
        T nonexistentTask = createTask("Test", "Test task", LocalDateTime.now());
        nonexistentTask.setId(100);
        taskJson = gson.toJson(nonexistentTask);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Код ответа должен быть 404");
    }

    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        T task = createTask("Test delete by id", "Test task", LocalDateTime.now());
        T task2 = createTask("Test delete by id 2", "Test task2", LocalDateTime.now().plusDays(1));
        task.setId(1);
        task2.setId(2);
        addToManager(task);
        addToManager(task2);

        String deletedTaskId = "2";
        String urlWithParams = url.toString() + "/" + deletedTaskId;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlWithParams))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");

        //задачи не должно быть в менеджере
        Assertions.assertFalse(getTasksFromManager().stream().anyMatch(task0 -> task0.getId() == 2));

        //удаление несуществующей задачи (повтор запроса на удаление задачи id:2)
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Код ответа должен быть 404");
    }

    public void checkTasksEquals(T task1, T task2) {
        Assertions.assertAll("Значения полей в задаче из метода и в задаче из менеджера не совпадают",
                () -> Assertions.assertEquals(task1.getId(), task2.getId(), "Id задач не совпадают"),
                () -> Assertions.assertEquals(task1.getName(), task2.getName(), "Названия задач не совпадают"),
                () -> Assertions.assertEquals(task1.getDescription(), task2.getDescription(), "Описания задач не совпадают"),
                () -> Assertions.assertEquals(task1.getStatus(), task2.getStatus(), "Статусы задач не совпадают"),
                () -> Assertions.assertEquals(task1.getDuration(), task2.getDuration(), "Продолжительность задач не совпадает"),
                () -> Assertions.assertEquals(task1.getStartTime(), task2.getStartTime(), "Время начала задач не совпадает")
        );
    }
}