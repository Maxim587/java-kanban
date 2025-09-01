package httpserver;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.educationmm.taskmanager.main.model.Epic;
import ru.educationmm.taskmanager.main.model.Subtask;
import ru.educationmm.taskmanager.main.model.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpServerEpicsTest extends AbstractHttpTaskManagerTasksTest<Epic> {

    public HttpServerEpicsTest() {
        this.url = URI.create("http://localhost:8080/epics");
    }

    @Override
    public Epic createTask(String name, String description, LocalDateTime startTime) {
        return new Epic(name, description);
    }

    @Override
    public Epic getTaskFromJson(JsonElement jsonElement) {
        return gson.fromJson(jsonElement, Epic.class);
    }

    @Override
    public List<Epic> getTasksFromManager() {
        return taskManager.getEpics();
    }

    @Override
    public void addToManager(Epic task) {
        taskManager.addTask(task);
    }

    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Test epic", "Test epic");
        taskManager.addTask(epic);
        taskManager.addTask(new Subtask("st1", "st1", epic.getId(), TaskStatus.NEW, 5, LocalDateTime.now()));
        taskManager.addTask(new Subtask("st2", "st2", epic.getId(), TaskStatus.NEW, 5, LocalDateTime.now().plusDays(1)));

        String urlWithParams = url.toString() + "/" + epic.getId() + "/subtasks";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlWithParams))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");

        //количество подзадач в эпике равно количеству в ответе
        String responseBody = new String(response.body().getBytes(StandardCharsets.UTF_8));
        JsonElement jsonElement = JsonParser.parseString(responseBody);
        Assertions.assertTrue(jsonElement.isJsonArray(), "Некорректное количество задач");
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertEquals(2, jsonArray.size(), "Некорректное количество задач");
    }

    @Override
    public void testUpdateTask() {
    }

    @Override
    public void testTaskIntersection() {
    }
}