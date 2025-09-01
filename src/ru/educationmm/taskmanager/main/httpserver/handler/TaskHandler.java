package ru.educationmm.taskmanager.main.httpserver.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.educationmm.taskmanager.main.model.TaskType;

import java.io.IOException;

public class TaskHandler implements HttpHandler {

    private final BaseHttpHandler baseHttpHandler;

    public TaskHandler(BaseHttpHandler baseHttpHandler) {
        this.baseHttpHandler = baseHttpHandler;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            baseHttpHandler.handleTaskRequests(exchange, TaskType.TASK);
        } catch (IOException e) {
            System.out.println("Ошибка обработки запроса " + exchange.getRequestURI().getPath());
            e.printStackTrace();
        }
    }
}
