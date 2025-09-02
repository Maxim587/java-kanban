package ru.educationmm.taskmanager.main.httpserver.handler;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import ru.educationmm.taskmanager.main.exception.ManagerSaveException;
import ru.educationmm.taskmanager.main.exception.NotFoundException;
import ru.educationmm.taskmanager.main.exception.TaskOverlapException;
import ru.educationmm.taskmanager.main.httpserver.util.DurationAdapter;
import ru.educationmm.taskmanager.main.httpserver.util.LocalDateTimeAdapter;
import ru.educationmm.taskmanager.main.model.Epic;
import ru.educationmm.taskmanager.main.model.Subtask;
import ru.educationmm.taskmanager.main.model.Task;
import ru.educationmm.taskmanager.main.model.TaskType;
import ru.educationmm.taskmanager.main.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .serializeNulls()
                .create();
    }

    public Gson getGson() {
        return gson;
    }

    protected void handleTaskRequests(HttpExchange exchange) throws IOException {
        if (!checkTaskId(exchange)) {
            sendNotFound(exchange);
            return;
        }

        String endpoint = getPathParts(exchange)[1];
        TaskType taskType;
        switch (endpoint) {
            case "tasks" -> taskType = TaskType.TASK;
            case "epics" -> taskType = TaskType.EPIC;
            case "subtasks" -> taskType = TaskType.SUBTASK;
            default -> {
                sendNotFound(exchange);
                return;
            }
        }

        try {
            switch (exchange.getRequestMethod()) {
                case "GET" -> handleTaskGetMethods(exchange, taskType);
                case "POST" -> handleTaskPostMethods(exchange, taskType);
                case "DELETE" -> handleTaskDeleteMethods(exchange, taskType);
                default -> sendNotFound(exchange);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (TaskOverlapException e) {
            sendHasOverlaps(exchange, e.getMessage());
        } catch (ManagerSaveException e) {
            exchange.sendResponseHeaders(500, -1);
            exchange.close();
        } catch (JsonSyntaxException | IllegalArgumentException e) {
            sendBadRequest(exchange);
        }
    }

    private void handleTaskGetMethods(HttpExchange exchange, TaskType taskType) throws IOException {
        String[] pathParts = getPathParts(exchange);
        Optional<Integer> taskIdOpt = getTaskId(pathParts);

        if (taskIdOpt.isEmpty()) {
            switch (taskType) {
                case TASK -> sendText(exchange, gson.toJson(taskManager.getTasks()), 200);
                case EPIC -> sendText(exchange, gson.toJson(taskManager.getEpics()), 200);
                case SUBTASK -> sendText(exchange, gson.toJson(taskManager.getSubtasks()), 200);
            }
        } else {
            int taskId = taskIdOpt.get();
            switch (taskType) {
                case TASK -> sendText(exchange, gson.toJson(taskManager.getTaskById(taskId)), 200);
                case EPIC -> {
                    if (pathParts.length == 4 && pathParts[3].equals("subtasks")) {
                        sendText(exchange, gson.toJson(taskManager.getEpicSubtasks(taskId)), 200);
                    } else {
                        sendText(exchange, gson.toJson(taskManager.getEpicById(taskId)), 200);
                    }
                }
                case SUBTASK -> sendText(exchange, gson.toJson(taskManager.getSubtaskById(taskId)), 200);
            }
        }
    }

    private void handleTaskPostMethods(HttpExchange exchange, TaskType taskType) throws IOException {

        InputStream inputStream = exchange.getRequestBody();
        String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        JsonElement jsonElement = JsonParser.parseString(requestBody);

        if (!jsonElement.isJsonObject()) {
            throw new IllegalArgumentException();
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        if (jsonObject.has("id")
                && !jsonObject.get("id").getAsString().isEmpty()
                && !jsonObject.get("id").getAsString().equals("0")) {
            try {
                jsonObject.get("id").getAsInt();
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException();
            }

            if (taskType.equals(TaskType.EPIC)) {
                throw new IllegalArgumentException();
            }

            updateTask(jsonElement, taskType);
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        } else {
            jsonElement.getAsJsonObject().addProperty("id", 0);
            String response = addTask(jsonElement, taskType);
            sendText(exchange, response, 201);
            exchange.close();
        }
    }

    private void handleTaskDeleteMethods(HttpExchange exchange, TaskType taskType) throws IOException {
        Optional<Integer> taskIdOpt = getTaskId(getPathParts(exchange));
        if (taskIdOpt.isEmpty()) {
            sendNotFound(exchange);
        } else {
            switch (taskType) {
                case TASK -> taskManager.deleteTaskById(taskIdOpt.get());
                case EPIC -> taskManager.deleteEpicById(taskIdOpt.get());
                case SUBTASK -> taskManager.deleteSubtaskById(taskIdOpt.get());
            }
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        }
    }

    protected void handleHistoryRequest(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getHistory()), 200);
    }

    protected void handlePrioritizedRequest(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getPrioritizedTasks()), 200);
    }

    private String addTask(JsonElement jsonElement, TaskType taskType) {
        return switch (taskType) {
            case TASK -> gson.toJson(taskManager.addTask(gson.fromJson(jsonElement, Task.class)));
            case EPIC -> {
                Epic epic = gson.fromJson(jsonElement, Epic.class);
                yield gson.toJson(taskManager.addTask(new Epic(epic.getName(), epic.getDescription())));
            }
            case SUBTASK -> gson.toJson(taskManager.addTask(gson.fromJson(jsonElement, Subtask.class)));
        };
    }

    private void updateTask(JsonElement jsonElement, TaskType taskType) {
        switch (taskType) {
            case TASK -> taskManager.updateTask(gson.fromJson(jsonElement, Task.class));
            case SUBTASK -> taskManager.updateTask(gson.fromJson(jsonElement, Subtask.class));
        }
    }

    private void sendText(HttpExchange exchange, String text, int respCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(respCode, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    private void sendNotFound(HttpExchange exchange) throws IOException {
        byte[] resp = "Объект не найден".getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(404, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    private void sendHasOverlaps(HttpExchange exchange, String message) throws IOException {
        byte[] resp = message.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(406, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    private void sendBadRequest(HttpExchange exchange) throws IOException {
        byte[] resp = "Тело запроса не соответствует спецификации".getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(400, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    private Optional<Integer> parseTaskId(String id) {
        try {
            return Optional.of(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            return Optional.of(-1);
        }
    }

    private Optional<Integer> getTaskId(String[] pathParts) {
        if (pathParts.length > 2) {
            return parseTaskId(pathParts[2]);
        }
        return Optional.empty();
    }

    private boolean checkTaskId(HttpExchange exchange) {
        Optional<Integer> taskIdOpt = getTaskId(getPathParts(exchange));
        return taskIdOpt.isEmpty() || taskIdOpt.get() != -1;
    }

    private String[] getPathParts(HttpExchange exchange) {
        return exchange.getRequestURI().getPath().split("/");
    }
}

