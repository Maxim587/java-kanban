package ru.educationmm.taskmanager.main.httpserver.handler;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class TaskHandler extends AbstractHandler {

    public TaskHandler(BaseHttpHandler baseHttpHandler) {
        super(baseHttpHandler);
    }

    @Override
    protected void handleRequests(HttpExchange exchange) throws IOException {
        baseHttpHandler.handleTaskRequests(exchange);
    }
}
